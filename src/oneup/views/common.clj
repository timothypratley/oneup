(ns oneup.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers]
        [hiccup.core]))

(defpartial layout [& content]
             (html
               (doctype :html5)
               "<html ng-app>"
               [:head
                [:title "oneup"]
                (include-css "/css/reset.css")
                (include-css "/css/bootstrap.min.css")
                (include-js "http://code.angularjs.org/angular-1.0.1.min.js")]
               [:body
                [:header.navbar.navbar-fixed-top
                 [:div.navbar-inner
                  [:div.container {:style "width:85%"}
                   [:a.btn.btn-navbar {:data-toggle "collapse"
                                       :data-target ".nav-collapse"}
                    [:span.icon-bar [:i]]
                    [:span.icon-bar]
                    [:span.icon-bar]]
                   [:a.brand {:href "/"}
                    [:strong "oneup"]]]]]
                content])
               "</html>")
