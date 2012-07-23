(ns oneup.tests)

(def pirate-summary (ref {}))

(dosync
  (alter pirate-summary
         assoc "blackbeard" {:proposals 1}))