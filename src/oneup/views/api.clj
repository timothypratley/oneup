(ns oneup.views.api
  (:require [oneup.views.common :as common])
  (:use [noir.core]
        [noir.response :only [json]]
        [oneup.models.domain]))

;Start or join a group
(defpage [:post "/plunder"] []
         (println "plunder"))

;As leader propose how to divide
;TODO why can't I use {keys [a b c d e]}?
(defpage [:post "/propose/:a/:b/:c/:d/:e"] {:keys [a b c d e]}
         (println "propose" a b c d e)
         (add-proposal-command (map #(Integer/parseInt %) [a b c d e])))

;As subordinate aye/ney
(defpage [:post "/vote/:vote"] {:keys [vote]}
         (println "vote" vote)
         (if (string? vote)
           (if (#{\y \Y} (first vote))
             (add-vote-command true))
           (if (#{\n \N} (first vote))
             (add-vote-command false))))
