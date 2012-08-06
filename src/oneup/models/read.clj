(ns oneup.models.read
  (:use [oneup.models.helper]))

;TODO why use ref here but agent for domain?
(def leaderboard (ref {}))
(def pirate-summaries (ref {}))
(def proposal-statistics (ref {}))

(def ninc (fnil inc 0))

(let [last-id (atom 0)]
  (defn next-id []
    (swap! last-id inc)))

(defmulti denormalize :type)

(defn update-pirate
  [ks f & args]
  (alter pirate-summaries
         #(apply update-in % ks f args)))

(defmethod denormalize :vote-added
  [vote]
  (alter pirate-summaries
         update-in [(:username vote)]
         reconcile
         [:update :vote-count ninc]))
  ;(dosync
    ;(update-pirate [(:pirate vote)]
           ;assoc-in [(vote :proposal-id) :votes (vote :rank)] (vote :value))))

(defn made [pirate-summary proposal]
  (println pirate-summary)
  (-> pirate-summary
      (update-in [:proposed-count] ninc)
      (update-in [:proposal-history] conj (proposal :id))
      (assoc :proposing proposal)))

(defmethod denormalize :proposal-added [proposal]
  (dosync
    ;(alter proposals
           ;assoc (proposal :id) proposal)
    (println proposal)
    (alter proposal-statistics
           update-in [(proposal :gold)] ninc)
    (update-pirate [(:username proposal)] made proposal)))

(defmethod denormalize :proposal-closed
  [closed]
  (dosync
    ))

(defmethod denormalize :proposal-accepted [proposal]
  (dosync
    (doseq [ii (range (count (:pirates proposal)))]
      (let [pirate ((proposal :pirates) ii)
            gold ((proposal :gold) ii)]
        (update-pirate [pirate :plunder-count] ninc)
        (update-pirate [pirate :gold] + gold)
        (if (= 0 ii)
          (update-pirate [pirate :success-count] ninc)
          (update-pirate [pirate :accept-count] ninc))))
    (alter leaderboard)))


(defmethod denormalize :proposal-rejected [proposal]
  (dosync
    (doseq [ii (range (count (:pirates proposal)))]
      (let [pirate ((proposal :pirates) ii)]
        (if (= 0 ii)
          (update-pirate [pirate :failure-count] ninc)
          (update-pirate [pirate :reject-count] ninc))))
    (alter leaderboard)))

(defmethod denormalize :user-added [user]
  (dosync
    (alter pirate-summaries
           assoc (:username user)
               (reconcile {} user
                 [:copy :when :joined]))))
