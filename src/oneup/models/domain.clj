(ns oneup.models.domain
  (:use [oneup.models.read]))

(def pirates (ref {}))
(def proposals (ref {}))
(def votes (ref {}))

(defmulti accept :type)

(defn store [event])
  
(defn publish [event]
  ;TODO: use a message bus instead
  (denormalize event))
  
; event handling
(defn raise [event]
  (store event)
  (accept event)
  (publish event))

(defmethod accept :add-pirate [pirate]
  (alter pirates assoc (:name pirate) (:joined pirate)))
(defn add-pirate-command [pirate]
  (dosync
    (if (not (@pirates (:name pirate)))
      (raise {:type :add-pirate
              :name pirate
              :joined (java.util.Date.)}))))

(let [last-id (ref 0)]
  (defn next-id []
    (dosync
      (alter last-id inc))))

(defmethod accept :add-proposal [proposal]
  (alter proposals
         assoc (next-id) proposal))
(defn gold? [g]
  (and (integer? g) (<= 0 g 10)))
(defn add-proposal-command[pirate gold]
  (dosync
    (cond
      (not (= 5 (count gold))) "an array of five integers from 0 to 10 which sums to 10"
      (not (every? gold? gold)) "integers must be from 0 to 10"
      (not (= 10 (reduce + gold))) "must sum to 10"
      :else (raise {:type :add-proposal
                    :pirates [pirate nil nil nil nil]
                    :votes [:yes nil nil nil nil]
                    :gold gold}))))

(defmethod accept :add-vote [vote]
  (alter votes assoc (next-id) vote))
(defn add-vote-command [b]
  (dosync
    (raise b)))
