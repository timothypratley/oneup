(ns oneup.views.index
  (:require [noir.session :as session])
  (:use [oneup.views.common]
        [oneup.models.read]
        [oneup.models.helper]
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

(defpage "/" []
         (layout
           [:div.ng-view]))

(defpage "/partials/about" []
         (html
           [:p "Yeargh, welcome ye to the Five O Pirates."]
           "Feedback: "
           (mail-to "timothypratley@gmail.com")))

(defn pirate
  [username]
  [:div
    [:h3 "Name: " username]
    [:div {:ng-show "pirate"}
      [:p "Joined: {{pirate.joined}}"]
      [:p "Full: {{pirate}}"]]
    [:div {:ng-show "!pirate"}
      "No such scurvy sea dog sails the high seas"]])

(defpage "/partials/harbor" []
         (html
           (pirate (session/get :username))
           (link-to "#/plunder" "Plunder!")))

(def gold-field
  [:div {:ng-repeat "g in gold"}
    [:ng-form {:name "f"}
     (text-field {:ng-model "g.gold"
                        :type "number"
                        :min 0
                        :max 10
                        :required true
                        :integer true}
          "g")
     [:span.error {:ng-show "f.g.$invalid"} "[X] "]
     [:span.error {:ng-show "f.g.$error.integer"} "Must be an integer"]
     [:span.error {:ng-show "f.g.$error.required"} "Required"]]])

(defpage "/partials/pirate" []
         (html (pirate "{{username}}")))

(defpage "/partials/propose" []
         (html
           [:form.css-form {:name "x"
                            :ng-submit "submit()"
                            :novalidate true}
             [:p "How should the gold be divided?"]
             gold-field
             (label "total" "Total: {{total()}}")
             [:br]
             [:span.error {:ng-show "x.$invalid"} "{{x.$error}}"]
             [:br]
             (submit-button {:ng-disabled "x.$invalid || total() != 10"} "Propose")]))

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

(defpage "/partials/leaderboard" []
  (html
    [:h3 "Leaderboard"]
    [:table
     [:tr {:ng-repeat "leader in leaders"}
      [:td "{{leader.username}}"]
      [:td "{{leader.gold}}"]
      [:td "{{leader.deaths}}"]
      [:td "{{leader.accepted}}"]]]))

