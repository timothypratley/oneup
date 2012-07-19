(ns oneup.views.api
  (:require [oneup.views.common :as common])
  (:use [noir.core]
        [noir.response :only [json]]
        [hiccup.core :only [html]]))

(defpage [:post "/vote"] []
         (json ))

(defpage [:post "/propose"] []
         (json ))
