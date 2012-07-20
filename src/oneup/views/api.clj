(ns oneup.views.api
  (:require [oneup.views.common :as common])
  (:use [noir.core]
        [noir.response :only [json]]
        [hiccup.core :only [html]]))

(defpage [:post "/vote"] []
         (json ))

(defn gold? [g]
  (and (integer? g) (<= 0 g 10)))
  
(defpage [:post "/propose"] v
         (println "hi")
         (println v)
         (cond
           (nil? v) "No gold sent"
           (not (vector? v)) "Send me an array please"
           (not (every? gold? v)) "Integers 0-10 only please"
           (not (= 10 (reduce + v))) "Must sum to 10"
           :else "Success!"))
