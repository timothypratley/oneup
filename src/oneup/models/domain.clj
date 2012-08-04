(ns oneup.models.domain
  (:use [oneup.models.helper]))


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
      (@publish e)))
  
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
    (get-in @domain [:user leader :proposed size]))

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
      :else (let [user (user username)]
              (if user
                (= password (user :password))
                (raise {:type :user-added
                        :username username
                        :password password})))))

  (defmethod accept :proposal-added
    [domain proposal]
    (assoc-in domain
              [:user (proposal :username) :proposal (proposal :size)]
              (reconcile {} proposal
                [:copy :gold])))

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
        (not (<= 2 size max-rank)) (str "an array of 2 to " max-rank " integers")
        (not (every? gold? gold)) (str "integers must be from 0 to " booty)
        (not (= booty (reduce + gold))) (str "must sum to " booty)
        (proposal username size) (str "already have an active proposal for " size)
        :else (raise {:type :proposal-added
                      :username username
                      :gold gold}))))

  (defn rank-update
    "Creates a pattern to update a vector field in the entity
     at location rank
     with a field from the event of the same name" 
    [entity & fields]
    (for [field fields]
      [:update field #(assoc % (:rank entity) (field entity))]))
    
  (defmethod accept :vote-added
    [domain vote]
    (apply update-in domain [:user (vote :leader) :proposal (vote :size)]
           reconcile vote
           (rank-update vote :username :when :vote)))

  ;public
  (defn add-vote-command
    "Add a vote"
    [username leader size rank vote]
    (if-let [p (proposal leader size)]
       (cond
        (not (<= 2 rank size max-rank)) "not a valid rank"
        (get (:party p) rank) "rank already voted"
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
