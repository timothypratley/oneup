(ns oneup.models.domain)

(def pirates (ref {}))
(def proposals (ref {}))
(def votes (ref {}))

(defn apply-event [v])

(defn store-event [event])
  
(defn publish-event [event]
  "success")
  
; event handling
(defn raise-event [event]
  (store-event event)
  (apply-event event)
  (publish-event event))

(defn add-pirate [pirate]
  (alter pirates assoc (:name pirate) pirate))
(defn event-handler [pirate]
  (add-pirate pirates pirate))
(defn add-pirate-command [pirate]
  (dosync
    (if (not (@pirates (:name pirate)))
      (raise-event pirate))))

; TODO
(def next-proposal-id)
(def next-vote-id)

(defn add-proposal [proposal]
  (dosync (alter proposals assoc (next-proposal-id) proposal)))
(defn event-handler [proposal]
  (add-proposal proposal))
(defn gold? [g]
  (and (integer? g) (<= 0 g 10)))
(defn add-proposal-command[v]
  (cond
    (not (= 5 (count v))) "an array of five integers"
    (not (every? gold? v)) "integers from 0 to 10"
    (not (= 10 (reduce + v))) "sum to 10"
    :else (raise-event v)))

(defn add-vote [vote]
  (dosync (alter votes assoc (next-vote-id) vote)))
(defn event-handler [vote]
  (add-vote vote))
(defn add-vote-command [b]
  (raise-event b))
    
