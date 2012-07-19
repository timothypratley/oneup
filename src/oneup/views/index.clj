(ns oneup.views.index
  (:require [oneup.views.common :as common])
  (:use [noir.core]
        [noir.response :only [json]]
        [hiccup.core]
        [hiccup.form-helpers]))

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

(defpage "/forum/:id" attrs 
         (json {:attrs attrs}))

(defn gold-field [name]
  (text-field {:ng-model name
               :type "number"
               :min 0
               :max 10
               :required true
               :ng-validate "integer"} (str "input-" name)))

(defpage "/propose" []
         (common/layout
           [:p "How should the gold be divided?"]
           [:form {:name "myForm"
                   :ng-submit "submit()"
                   :ng-controller "ProposalController"}
            (gold-field "gold1")
            (gold-field "gold2")
            (gold-field "gold3")
            (gold-field "gold4")
            (gold-field "gold5")
            [:br]
            [:span.error {:ng-show "myForm.$invalid"} "{{myForm.$error}}"]
            [:br]
            (submit-button {
                            :id "submit"
                            ;:disabled "myForm.$invalid"
                            }
              "Propose")]))
