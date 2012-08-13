(ns oneup.models.domain
  (:use [oneup.models.helper]))

(def max-size 5)
(def min-size 2)
(def booty 10)

(let [domain (agent {:user {}})
      publish (ref (fn [event]))
      store (ref (fn [event]))]

  (defmulti accept
  "Updates the domain model according to the event that occured.
   This should only be called from raise."
  (fn [domain event] (event :type)))

  ;public
  (defn publisher
    "Sets the function that will publish events"
    [f]
    (dosync
      (ref-set publish f)))
  
  ;public
  (defn storer
    "Sets the function that will store events"
    [f]
    (dosync
      (ref-set store f)))

  ;public
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
    (get-in @domain [:user leader :proposal size rank :vote]))

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
      (nil? username) "must supply a username"
      (nil? password) "must supply a password"
      (user username) "user already exists"
      :else (raise {:type :user-added
                    :username username
                    :password password})))

  (defmethod accept :proposal-added
    [domain proposal]
    (update-in domain
               [:user (proposal :username) :proposal (proposal :size)]
               reconcile proposal
               [:copy :gold]))

  (defmethod accept :proposal-closed
    [domain proposal]
    (dissoc domain
            :user (proposal :leader) :proposal (proposal :size)))

  (defn gold? [g]
    (and (integer? g) (<= 0 g booty)))

  ;public
  (defn add-proposal-command
    "Add a proposal"
    [username gold]
    (let [size (count gold)]
      ;TODO: make an error accumulator
      (cond
        (not (user username)) (str "no such user " username)
        (not (<= min-size size max-size)) (str "an array of " min-size " to " max-size " integers")
        (not (every? gold? gold)) (str "integers must be from 0 to " booty)
        (not (= booty (reduce + gold))) (str "must sum to " booty)
        (proposal username size) (str "already have an active proposal for " size)
        :else (raise {:type :proposal-added
                      :username username
                      :size size
                      :gold gold}))))
    
  (defmethod accept :vote-added
    [domain vote]
    (update-in domain [:user (vote :leader) :proposal (vote :size) (vote :rank)]
           reconcile vote
           [:copy :username]
           [:copy :vote]))

  ;public
  (defn add-vote-command
    "Add a vote"
    [username leader size rank vote]
    (if-let [p (proposal leader size)]
      (cond
        (not (user username)) (str "no such user " username)
        (not (user leader)) (str "no such leader " leader)
        (not (<= min-size rank size max-size)) "not a valid rank"
        (get-in p [size rank]) "rank already voted"
        (= username leader) "cannot vote on your own proposal"
        :else (do
                (raise {:type :vote-added
                        :username username
                        :leader leader
                        :size size
                        :rank rank
                        :vote vote})
                (when (= (- size 2) (count (p :vote)) 
                  (raise {:type :proposal-closed
                          :leader leader
                          :size size})))))
      "no such proposal")))

