(ns oneup.models.read
  (:use [oneup.models.helper]))

;TODO why use ref here but agent for domain?
(def leaderboard (ref []))
(def users (ref {}))
(def proposal-statistics (ref {}))

(def update-user (partial alter users update-in))
(def ninc (fnil inc 0))

(defn score
  "Calculate the score for a user"
  [username]
  (get-in @users [username :gold] 0))

(defn swapv [v i1 i2] 
   (assoc v i2 (v i1) i1 (v i2)))

(defn update-leaderboard
  [username]
  (dosync
    ;start at the current rank
    (let [rank (atom (get-in @users [username :rank]))
          new-score (score username)
          players (count @leaderboard)]
      ;calc the new score and save it to user
      (alter users assoc-in [username :score] new-score)
      ;swap ranks upward, updating the swappie as we go
      (while (and (pos? @rank)
                  (< ((@users (@leaderboard (dec @rank))) :score) new-score))
        (alter leaderboard swapv @rank (dec @rank))
        (alter users assoc-in [(@leaderboard @rank) :rank] @rank)
        (swap! rank dec))
      ;or swap ranks downward
      (while (and (< @rank (dec players))
                  (> ((@users (leaderboard (inc @rank))) :score) new-score))
        (alter leaderboard swapv @rank (inc @rank))
        (alter users assoc-in [(@leaderboard @rank) :rank] @rank)
        (swap! rank inc))
      ;until we reach the right rank, and save the new rank
      (alter users assoc-in [username :rank] @rank))))

(defmulti denormalize :type)

(defmethod denormalize :proposal-added [proposal]
  (dosync
    (let [leader (proposal :username)
          size (count (proposal :gold))
          nils (repeat (dec size) nil)
          make-vec #(vec (cons % nils))]
      (alter proposal-statistics
             update-in [(proposal :gold)] ninc)
      (alter users
             update-in [leader]
             reconcile proposal
             [:update :proposed-count ninc]
             [:update :proposal-history conj :gold])
      (alter users
             update-in [leader :proposal size]
             reconcile proposal
             [:copy :gold]
             [:setf :users make-vec :username]
             [:setf :voted make-vec :when]
             [:set :vote (make-vec true)])
      (update-leaderboard leader))))

(defn proposal-accepted [leader proposal]
    (update-user [leader :success-count] ninc)
    (doseq [ii (range (count (proposal :users)))]
      (let [username ((proposal :users) ii)
            gold ((proposal :gold) ii)]
        (update-user [username :gold] (fnil + 0) gold)
        (update-user [username :plunder-count] ninc)
        (update-leaderboard username))))

(defn proposal-rejected [leader proposal]
    (update-user [leader :failure-count] ninc)
    (doseq [ii (range (count (proposal :users)))]
      (let [username ((proposal :users) ii)]
        (update-user [username :mutiny-count] ninc)
        (update-leaderboard username))))

(defn check-votes [leader size]
  (let [p (get-in @users [leader :proposal size])]
    (when (= (count (remove nil? (p :vote))) size)
      (if (>= (count (filter true? (p :vote))) (/ size 2))
        (proposal-accepted leader p)
        (proposal-rejected leader p))
      (alter users dissoc-in [leader :proposal size]))))

(defmethod denormalize :vote-added
  [vote]
  (dosync
    (let [username (vote :username)
          leader (vote :leader)
          size (vote :size)
          assoc-dec #(assoc %1 (dec %2) %3)]
      (alter users
             update-in [username]
             reconcile vote
             [:update :vote-count ninc])
      (alter users
             update-in [leader :proposal size]
             reconcile vote
             [:update :users assoc-dec :rank :username]
             [:update :voted assoc-dec :rank :when]
             [:update :vote assoc-dec :rank :vote])
      (when-not (check-votes leader size)
        (update-leaderboard username)
        (update-leaderboard leader)))))

(defmethod denormalize :user-added [user]
  (dosync
    (let [username (user :username)]
      (update-user [username]
                   reconcile user
                   [:copy :when :joined]
                   [:copy :password]
                   [:set :score 0]
                   [:set :rank (count @leaderboard)])
      (alter leaderboard conj username)
      (update-leaderboard username))))

