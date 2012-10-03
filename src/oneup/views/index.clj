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

(def login
  [:div#login.modal.hide.fade {:tabindex -1
                              :role "dialog"
                              :aria-labelledby "Login"
                              :aria-hidden "true"}
  [:div.modal-header
   "Login to Oneup"
   [:button.close {:type "button"
                   :data-dismiss "modal"
                   :aria-hidden "true"} "x"]]
  [:div.modal-body
   [:form {:ng-controller "LoginCtrl"
           :ng-submit "submit()"
           :novalidate true}
    [:div (label "username" "Username")
     (text-field {:ng-model "username"} "username")]
    [:div (label "password" "Password")
     (password-field {:ng-model "password"} "password")]      
    (submit-button "Login")]]
  ;TODO: should have a modal-footer with submit, but then no form?
  ])

(def header
  [:header.navbar.navbar-fixed-top {:ng-controller "TopCtrl"}
   [:div.navbar-inner
    [:a.brand {:href "/#/"}
     [:img {:src "/img/favicon.ico" :width "20" :height "20"}]
     [:strong "oneup"]]
    [:ul.nav
     [:li.divider-vertical]
     [:li (link-to "/#/about" "About")]
     [:li.divider-vertical]
     [:li (link-to "/#/leaderboard" "Leaderboard")]
     [:li.divider-vertical]
     [:li (link-to "/#/harbor" "Harbor")]
     [:li.divider-vertical]]
    [:div.login.ng-cloak.pull-right {:ng-show "!user.username"}
     (submit-button {:ng-click "login()"} "Login")]
    [:div.logout.ng-cloak.pull-right {:ng-show "user.username"}
     [:span "{{user.username}}"]
     (submit-button {:ng-click "logout()"} "logout")]]])

(defpage "/" []
         (html5
            [:head
             [:title "oneup"]
             [:meta {:name "viewport"
                     :content "width=device-width"
                     :initial-scale "1.0"}]
             [:link {:rel "icon"
                     :href "/img/favicon.ico"
                     :type "image/x-icon"}]
             [:link {:rel "shortcut"
                     :href "/img/favicon.ico"
                     :type "image/x-icon"}]
             (include-css "/css/bootstrap.min.css")
             (include-css "/css/oneup.css")
             (include-css "/css/bootstrap-responsive.min.css")]
             [:body {:authenticate "login"}
              header
              login
              [:div.ng-view "Loading..."]

              (include-js "/js/jquery-1.8.0.min.js")
              (include-js "/js/bootstrap.min.js")
              (include-js "/js/angular-1.0.1.min.js")
              (include-js "/js/angular-resource-1.0.1.min.js")
              (include-js "/js/http-auth-interceptor.js")
              (include-js "/js/services.js")
              (include-js "/js/controllers.js")
              (include-js "/js/oneup.js")]))

(defpage "/f" []
         (html
           "Your name:" [:input {:type "text"
                                 :ng-model "yourname"
                                 :placeholder "World"}]
           [:p "Hello {{yourname || 'World'}}"]
           [:ul {:ng-controller "ForumCtrl"}
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

(defpage "/partials/about" []
         (html
           [:p "Yeargh, welcome ye to the Five O Pirates."]
           "Feedback: "
           (mail-to "timothypratley@gmail.com")))

(def pirate
  [:div
    [:h3 "Name: {{pirate.username}}"]
    [:div {:ng-show "pirate"}
      [:p "Joined: {{pirate.joined}}"]
      [:p "Full: {{pirate}}"]]
    [:div {:ng-show "!pirate"}
      "No such scurvy sea dog sails the high seas"]])

(defpage "/partials/harbor" []
         (html
           pirate
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
         (html pirate))

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
     [:tr {:ng-repeat "pirate in leaderboard"}
      [:td {:ng-repeat "col in pirate"} "{{col}}"]]]))

