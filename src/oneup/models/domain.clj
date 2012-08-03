(ns oneup.models.domain
  (:use [oneup.models.helper]))


(defmulti accept
  "Updates the domain model according to the event that occured.
   This should only be called from raise."
  (fn [domain event] (event :type)))

(let [domain (agent {:user {}
                     :proposal {}
                     :vote {}})
      publish (ref (fn [event]))
      store (ref (fn [event]))]
  
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
      (@publish e))))


(defmethod accept :user-added
  [domain u]
  (assoc-in domain [:user (:username u)]
             (reconcile {} u
               [:copy :password]
               [:copy :when :joined])))

;public
(defn add-user-command
  "Add a user"
  [username password]
  (let [user ((@domain :user) username)]
    (if user
      (= password (user :password))
      (boolean (raise {:type :user-added
                       :username username
                       :password password})))))


(let [last-id (atom 0)]
  (defn next-id []
    (swap! last-id inc)))

(defmethod accept :proposal-added
  [domain p]
  (let [id (next-id)]
    (assoc-in domain [:proposal id]
              (reconcile {} p
                [:copy :user]
                [:copy :vote]
                [:create :voted [(p :when)]]
                [:copy :gold]))
    (assoc-in domain [:user (first (p :user)) :proposed] id)))

(defn gold? [g]
  (and (integer? g) (<= 0 g 10)))

;public
(defn add-proposal-command
  "Add a proposal"
  [user gold]
  (cond
    (not (= 5 (count gold))) "an array of five integers from 0 to 10 which sums to 10"
    (not (every? gold? gold)) "integers must be from 0 to 10"
    (not (= 10 (reduce + gold))) "must sum to 10"
    (get-in @domain [:user user :proposed]) "already have an active proposal"
    :else (raise {:type :proposal-added
                  :user [user nil nil nil nil]
                  :vote [:yes nil nil nil nil]
                  :gold gold})))


(defmethod accept :vote-added
  [domain v]
  (update-in domain [:proposal :vote (v :position)] (v :value)))

;public
(defn add-vote-command
  "Add a vote"
  [user b]
  (raise {:type :vote-added
          :user user
          :vote b}))
