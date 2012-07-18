(ns oneup.domain)

(def pirates (ref {}))
(def proposals (ref {}))
(def votes (ref {}))

(defn raise-event [event]
  (store-event event)
  (event-handler event)
  (publish-event event))

(defn add-pirate [pirate]
  (alter pirates assoc (:name pirate) pirate))
(defn event-handler [pirate]
  (add-pirate pirates pirate))
(defn add-pirate-command [pirate]
  (dosync
    (if (not (@pirates (:name pirate)))
      (raise-event pirate))))

(defn add-proposal [proposal]
  (dosync (alter proposals assoc (next-proposal-id) proposal)))
(defn event-handler [proposal]
  (add-proposal proposal))

(defn add-vote [vote]
  (dosync (alter votes assoc (next-vote-id) vote)))
(defn event-handler [vote]
  (add-vote vote))
