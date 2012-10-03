(ns oneup.models.read
  (:use [oneup.models.helper]))

;TODO why use ref here but agent for domain?
(def leaderboard (ref []))
(def user-summaries (ref {}))
(def proposal-statistics (ref {}))

(def update-user (partial alter user-summaries update-in))
(def ninc (fnil inc 0))

(defn score
  "Calculate the score for a user"
  [username]
  (get-in @user-summaries [username :gold] 0))

(defn swapv [v i1 i2] 
   (assoc v i2 (v i1) i1 (v i2)))

(defn update-leaderboard
  [username]
  (dosync
    ;start at the current rank
    (let [rank (atom (get-in @user-summaries [username :rank]))
          new-score (score username)]
      ;calc the new score and save it to user
      (alter user-summaries assoc-in [username :score] new-score)
      ;swap ranks upward, updating the swappie as we go
      #_(println "DEBUG " username " : " (@user-summaries username))
      #_(println "DEBUG leaderboard: " @leaderboard)
      #_(println "DEBUG rank: " @rank)
      (while (and (pos? @rank)
                  (< ((@user-summaries (@leaderboard (dec @rank))) :score) new-score))
        (alter leaderboard swapv @rank (dec @rank))
        (alter user-summaries assoc-in [(@leaderboard @rank) :rank] @rank)
        (swap! rank dec))
      ;or swap ranks downward
      (while (and (< @rank (dec (count @leaderboard)))
                  (> ((@user-summaries (leaderboard (inc @rank))) :score) new-score))
        (alter leaderboard swapv @rank (inc @rank))
        (alter user-summaries assoc-in [(@leaderboard @rank) :rank] @rank)
        (swap! rank inc))
      ;until we reach the right rank, and save the new rank
      (alter user-summaries assoc-in [username :rank] @rank))))

(defmulti denormalize :type)

(defmethod denormalize :proposal-added [proposal]
  (dosync
    (let [leader (proposal :username)
          nils (repeat (dec (count (proposal :gold))) nil)
          make-vec #(vec (cons % nils))]
      (alter proposal-statistics
             update-in [(proposal :gold)] ninc)
      (alter user-summaries
             update-in [leader]
             reconcile proposal
             [:update :proposed-count ninc]
             [:update :proposal-history conj :gold])
      (alter user-summaries
             update-in [leader :proposal :size]
             reconcile proposal
             [:copy :gold]
             [:setf :users make-vec :username]
             [:setf :voted make-vec :when]
             [:set :vote (make-vec true)])
      (update-leaderboard leader))))

(defmethod denormalize :vote-added
  [vote]
  (dosync
    (alter user-summaries
           update-in [(:username vote)]
           reconcile vote
           [:update :vote-count ninc])
    (alter user-summaries
           update-in [(vote :leader) :proposal (vote :size)]
           reconcile vote
           [:update :users assoc :rank :username]
           [:update :voted assoc :rank :when]
           [:update :vote assoc :rank :vote])
    (update-leaderboard (vote :username))
    (update-leaderboard (vote :leader))))

(defmethod denormalize :proposal-accepted [proposal]
  (dosync
    (update-user [(first (proposal :users)) :success-count] ninc)
    (doseq [ii (range (count (proposal :users)))]
      (let [username ((proposal :users) ii)
            gold ((proposal :gold) ii)]
        (update-user [username :gold] + gold)
        (update-user [username :plunder-count] ninc)
        (update-leaderboard username)))))


(defmethod denormalize :proposal-rejected [proposal]
  (dosync
    (update-user [(first (proposal :users)) :failure-count] ninc)
    (doseq [ii (range (count (proposal :users)))]
      (let [username ((proposal :users) ii)]
        (update-user [username :mutiny-count] ninc)
        (update-leaderboard username)))))

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

