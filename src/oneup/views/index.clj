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

(defn gold-field [number]
  (let [name (str "gold" number)]
    [:p (text-field {:ng-model (str "gold[" number "]")
                     :type "number"
                     :min 0
                     :max 10
                     :required true
                     :integer true}
                     name)
     [:span.error {:ng-show (str "myForm." name ".$invalid")} (str "{{myForm." name ".$error}}")]]))

(defpage "/propose" []
         (common/layout
           [:p "How should the gold be divided?"]
           [:form.css-form {:name "myForm"
                            :ng-controller "ProposalController"
                            :novalidate true
                            :ng-submit "submit()"}
            (gold-field 0)
            (gold-field 1)
            (gold-field 2)
            (gold-field 3)
            (gold-field 4)
            (label "total" "Total: {{total()}}")
            [:br]
            [:span.error {:ng-show "myForm.$invalid"} "{{myForm.$error}}"]
            [:br]
            (submit-button {:id "submit"
                            :ng-disabled "myForm.$invalid || total() != 10"}
                           "Propose")]))
