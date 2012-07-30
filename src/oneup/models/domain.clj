(ns oneup.models.domain)

(def world (agent {:user {}
                   :proposal {}
                   :vote {}}))

(defmulti accept (fn [world event] (event :type)))

(let [publish (ref (fn [event]))
      store (ref (fn [event]))]
  
  (defn publisher [f]
    (dosync
      (ref-set publish f)))
  (defn storer [f]
    (dosync
      (ref-set store f)))

  (defn raise [event]
    ;TODO: how to make this sequential?
    (let [e (assoc event :when (java.util.Date.))]
      (@store e)
      (send world accept e)
      (@publish e))))


(defmethod accept :user-added [world u]
  (assoc-in world [:user (:username u)] u))

(defn add-user-command [username password]
  (let [user ((@world :user) username)]
    (if user
      (= password (user :password))
      (boolean (raise {:type :user-added
                       :username username
                       :password password})))))

(let [last-id (atom 0)]
  (defn next-id []
    (swap! last-id inc)))

(defmethod accept :proposal-added [world p]
  (assoc-in world [:proposal (next-id)] p))

(defn gold? [g]
  (and (integer? g) (<= 0 g 10)))
(defn add-proposal-command[user gold]
  (cond
    (not (= 5 (count gold))) "an array of five integers from 0 to 10 which sums to 10"
    (not (every? gold? gold)) "integers must be from 0 to 10"
    (not (= 10 (reduce + gold))) "must sum to 10"
    :else (raise {:type :proposal-added
                  :user [user nil nil nil nil]
                  :vote [:yes nil nil nil nil]
                  :gold gold})))


(defmethod accept :vote-added [world v]
  (assoc world [:vote (next-id)] v))

(defn add-vote-command [user b]
  (raise {:type :vote-added
          :user user
          :vote b}))
