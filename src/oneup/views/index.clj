(ns oneup.views.welcome
  (:require [oneup.views.common :as common])
  (:use [noir.core]
        [noir.response :only [json]]
        [hiccup.core :only [html]]))

(defpage "/" []
         (common/layout
           "Your name:" [:input {:type "text"
                                 :ng-model "yourname"
                                 :placeholder "World"}]
           [:p "Hello {{yourname || 'World'}}"]
           [:ul {:ng-controller "ForumController"}
            [:li {:ng-repeat "post in posts"}
             "{{post.date}}{{post.author}}{{post.title}}"]]))

(defpage "/forum" []
         (json [{:title "I love rewriting our site"
                 :author "root"
                 :date "20120714"}
                {:title "who let the dogs out?"
                 :author "wintermute"
                 :date "20120715"}
                {:title "how to be an elite ninja"
                 :author "defeat"
                 :date "20120716"}]))

(defpage [:get "/forum/:id"] attrs 
         (json {:attrs attrs}))
