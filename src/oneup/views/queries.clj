(ns oneup.views.queries
  (:require [oneup.views.common :as common])
  (:use [noir.core]
        [noir.validation]
        [noir.response :only [json]]
        [oneup.models.read]))

(defpage "/leaderboard" []
         (println "leaderboard")
         (@leaderboard))

(defpage "/pirate/:name" {:keys [name]}
         (println "pirate:" name)
         (@pirate-summary name))

(defpage "/stats/:a/:b/:c/:d/:e" {:keys [a b c d e]}
         (println "stats:" [a b c d e])
         (@proposal-statistics [a b c d e]))
