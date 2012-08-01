(ns oneup.views.common
  (:use [noir.core]
        [hiccup.form-helpers]
        [hiccup.page-helpers]
        [hiccup.core]))

(defpartial layout [& content]
            (html
              (doctype :html5)
              [:html {:ng-app "oneup"
                      :ng-controller "TopController"}
               [:head
                [:title "oneup"]
                (include-css "/css/reset.css")
                (include-css "/css/bootstrap.min.css")
                (include-css "/css/oneup.css")]
               [:body
                [:header.navbar.navbar-fixed-top
                 [:div.navbar-inner
                  [:div.container {:style "width:85%"}
                   [:a.brand {:href "/"} [:strong "oneup"]]
                   "Feedback: "
                   (mail-to "timothypratley@gmail.com")
                   [:div.logout.ng-cloak {:ng-show "username"}
                    [:span "{{username}}"]
                    (submit-button {:ng-click "logout()"} "logout")]]]]
                content
                (include-js "/js/angular-1.0.1.min.js")
                (include-js "/js/angular-resource-1.0.1.min.js")
                (include-js "/js/services.js")
                (include-js "/js/controllers.js")
                (include-js "/js/oneup.js")]]))
