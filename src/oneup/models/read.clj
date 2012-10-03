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

;TODO: choose a better datastructure
; a sorted set of pairs?
; a map of ranks to pairs?
; a linked list of pairs?
(defn update-leaderboard
  [username]
  (dosync
    ;calc the new score (and save it to user)
    ;find the new rank (and save it to user)
    ;update and save all the users with new ranks between old and new
    ;move them in the vector
    (alter leaderboard assoc username (score username))))

(defmulti denormalize :type)

(defmethod denormalize :proposal-added [proposal]
  (println proposal)
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
    (println "VOTE-ADDED " vote)
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
    (update-user [(:username user)]
                 reconcile user
                 [:copy :when :joined]
                 [:copy :password])
    (alter leaderboard conj user)
    (update-leaderboard (:username user))))

