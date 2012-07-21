(ns oneup.views.api
  (:require [oneup.views.common :as common])
  (:use [noir.core]
        [noir.response :only [json]]
        [oneup.models.domain]))

(defpage [:post "/vote"] []
         (json ))
 
;TODO why can't I use {keys [a b c d e]}?
(defpage [:post "/propose/:a/:b/:c/:d/:e"] {a :a b :b c :c d :d e :e}
         (println "hi")
         (println a b c d e)
         (add-proposal-command (map #(Integer/parseInt %) [a b c d e])))
