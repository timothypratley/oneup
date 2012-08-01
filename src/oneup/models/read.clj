(ns oneup.models.read)

(def leaderboard (ref {}))
(def pirate-summaries (ref {}))
(def proposal-statistics (ref {}))

(def ninc (fnil inc 0))

(defmulti denormalize :type)

(defn update-pirate [ks f & args]
  (alter pirate-summaries
         #(apply update-in % ks f args)))

(defmethod denormalize :vote-added [vote])
  ;(dosync
    ;(update-pirate [(:pirate vote)]
           ;assoc-in [(vote :proposal-id) :votes (vote :rank)] (vote :value))))

(defn made [pirate-summary proposal]
  (println pirate-summary)
  (-> pirate-summary
      (update-in [:proposed-count] ninc)
      (update-in [:proposal-history] conj (proposal :id))
      (assoc :proposing (proposal :id))))

(defmethod denormalize :proposal-added [proposal]
  (dosync
    ;(alter proposals
           ;assoc (proposal :id) proposal)
    (println proposal)
    (alter proposal-statistics
           update-in [(proposal :gold)] ninc)
    (update-pirate [(first (:pirates proposal))] made proposal)))

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
  (println "denormalize " user)
  (dosync
    (alter pirate-summaries
           assoc (:username user) (:when user))))