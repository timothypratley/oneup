(ns oneup.views.queries
  (:use [oneup.views.common]
        [oneup.models.read]
        [noir.core]
        [cheshire.core]))

(defpage "/leaderboard" []
         (println "leaderboard")
         (generate-string @leaderboard))

(defpage "/pirate/:name" {:keys [name]}
         (generate-string (@user-summaries name)))

(defpage "/stats/:a/:b/:c/:d/:e" {:keys [a b c d e]}
         (println "stats:" [a b c d e])
         (generate-string (@proposal-statistics [a b c d e])))

