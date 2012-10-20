(ns oneup.models.domain
  (:use [clojure.string :only [blank?]]
        [oneup.models.helper]))

(let [domain (agent {:user {}})
      publish (ref (fn [event]))
      store (ref (fn [event]))
      max-proposal 5
      min-proposal 2
      booty 10]

  (defmulti accept
    "Updates the domain model according to the event that occured.
     This should only be called from raise."
    (fn [domain event] (event :type)))

  (defn publisher
    "Sets the function that will publish events"
    [f]
    (dosync
      (ref-set publish f)))
  
  (defn storer
    "Sets the function that will store events"
    [f]
    (dosync
      (ref-set store f)))

  (defn raise
    "Accepts the event to the domain model.
     The only updates to the domain model are done through raising events.
     The event is then stored (so we can recreate the domain model)
     and published (so read models can denormalize the event)."
    [event]
    ;TODO: how to make this sequential?
    (let [e (assoc event :when (java.util.Date.))]
      (@store e)
      (send domain accept e)
      (@publish e)
      @domain))
  
  ;public
  (defn hydrate
    "Accepts and publishes an event without storing it.
     Use only to load the domain and read model from an event store."
    [event]
    (send domain accept event)
    (@publish event))
  
  (defn user
    "Get a user"
    [username]
    (get-in @domain [:user username]))
  
  (defn proposal
    "Get a proposal"
    [leader size]
    (get-in @domain [:user leader :proposal size]))

  (defn get-vote
    "Get a vote"
    [leader size rank]
    (get-in @domain [:user leader :proposal size :votes rank :vote]))

  (defmethod accept :user-added
    [domain u]
    (assoc-in domain [:user (:username u)]
               (reconcile {} u
                 [:copy :password])))

  ;public
  (defn add-user-command
    "Add a user"
    [username password]
    (cond
      (blank? username)
        "must supply a username"
      (blank? password)
        "must supply a password"
      (user username)
        "user already exists"
      :else
        (raise {:type :user-added
                :username username
                :password password})))

  (defmethod accept :plunder-begins
    [domain plunder]
    (update-in domain [:user (plunder :username) :plunder]
               reconcile plunder
               [:copy :action]
               [:copy :size]
               [:copy :gold]))

  (defn need-proposal
    []
    (if (> 5 (count (map :proposal (domain :user))))
      #_(select a size) 5))

  (defn need-vote
    []
    #_(randomly select a proposal, size, rank))

  (defn begin-plunder
    "Server assigns you to propose a split or vote on one"
    [username]
    (if-let [size (need-proposal)]
      (raise {:type :plunder-begins
              :action :propose
              :size size})
      (raise {:type :plunder-begins
              :action :vote
              :gold (need-vote)
              :rank 2})))

  (defmethod accept :proposal-added
    [domain proposal]
    (update-in domain [:user (proposal :username) :proposal (proposal :size)]
               reconcile proposal
               [:copy :gold]))

  (defn- gold? [g]
    (and (integer? g) (<= 0 g booty)))

  ;public
  (defn add-proposal-command
    "Add a proposal"
    [username gold]
    (let [size (count gold)]
      ;TODO: make an error accumulator
      (cond
        (not (user username))
          (str "no such user " username)
        (not (<= min-proposal size max-proposal))
          (str "an array of " min-proposal " to " max-proposal " integers")
        (not (every? gold? gold))
          (str "integers must be from 0 to " booty)
        (not (= booty (reduce + gold)))
          (str "must sum to " booty)
        (proposal username size)
          (str "already have an active proposal for " size)
        :else
          (raise {:type :proposal-added
                  :username username
                  :size size
                  :gold gold}))))

  ; TODO:
  ; good - no logic in command handler
  ; bad - duplicating 'reactive events' in domain and read model
  (defn check-proposal-closed [ps vote]
    (let [size (vote :size)]
      (if (>= (count (get-in ps [size :votes])) (dec size))
        (dissoc ps size)
        ps)))

  (defn update-proposal [p vote]
    (update-in p [(vote :size) :votes (vote :rank)]
               reconcile vote
               [:copy :username]
               [:copy :vote]))
    
  (defmethod accept :vote-added
    [domain vote]
    (update-in domain [:user (vote :leader) :proposal]
               #(-> %
                 (update-proposal vote)
                 (check-proposal-closed vote))))

  ;public
  (defn add-vote-command
    "Add a vote"
    [username leader size rank vote]
    (let [p (proposal leader size)]
      (cond
        (not (user username))
          (str "no such user " username)
        (not (user leader))
          (str "no such leader " leader)
        (not (<= min-proposal rank size max-proposal))
          "not a valid rank"
        (nil? p)
          "no such proposal"
        (get-in p [rank])
          "rank already voted"
        (= username leader)
          "cannot vote on your own proposal"
        (some (partial = username) (map :username (vals (p :votes))))
          "cannot vote as more than one rank"
        :else
          (raise {:type :vote-added
                  :username username
                  :leader leader
                  :size size
                  :rank rank
                  :vote vote})))))

