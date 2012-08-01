(ns oneup.views.index
  (:require [noir.session :as session])
  (:use [oneup.views.common]
        [oneup.models.read]
        [noir.core]
        [noir.response :only [json]]
        [hiccup.core]
        [hiccup.form-helpers]
        [hiccup.page-helpers]))

(defpage "/f" []
         (layout
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


(defpage "/" []
         (layout
           [:div.ng-view]))

(defpage "/partials/harbor" []
         (html
           [:span (str (@pirate-summaries (session/get :username)))]
           (link-to "#/plunder" "Plunder!")))

(defpage "/partials/propose" []
         (html
           [:p "How should the gold be divided?"]
           [:form.css-form {:name "myForm"
                            :ng-submit "submit()"
                            :novalidate true}
            (gold-field 0)
            (gold-field 1)
            (gold-field 2)
            (gold-field 3)
            (gold-field 4)
            (label "total" "Total: {{total()}}")
            [:br]
            [:span.error {:ng-show "myForm.$invalid"} "{{myForm.$error}}"]
            [:br]
            (submit-button {:ng-disabled "myForm.$invalid || total() != 10"}
                           "Propose")]))

(defpage "/partials/vote" []
         (html
           [:p "Do you accept the proposal?"]
           [:form.css-form {:novalidate true}
            (submit-button {:ng-click "submit('yes')"} "Aye!")
            (submit-button {:ng-click "submit('no')"} "Avast!")]))

(defpage "/partials/login" []
         (html
           [:form.css-form {:novalidate true
                            :ng-submit "submit()"}
            [:div (label "username" "What be yer name?")
             (text-field {:ng-model "username"} "username")]
            [:div (label "password" "And plunderin' password?")
             (password-field {:ng-model "password"} "password")]
            (submit-button "Hoist the mainsail!")]))
