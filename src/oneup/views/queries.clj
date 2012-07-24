(ns oneup.views.queries
  (:use [oneup.views.common]
        [oneup.models.read]
        [noir.core]
        [noir.validation]
        [noir.response :only [json]]))

(defpage "/leaderboard" []
         (println "leaderboard")
         (@leaderboard))

(defpage "/pirate/:name" {:keys [name]}
         (println "pirate:" name)
         (layout
           (@pirate-summaries name)))

(defpage "/stats/:a/:b/:c/:d/:e" {:keys [a b c d e]}
         (println "stats:" [a b c d e])
         (layout
           (@proposal-statistics [a b c d e])))
