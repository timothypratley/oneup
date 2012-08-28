(ns oneup.views.queries
  (:require [noir.session :as session]
            [noir.response :as response])
  (:use [oneup.views.common]
        [oneup.models.read]
        [cheshire.core]
        [noir.core]))

(defn user-line
  [username]
  (let [user (@user-summaries username)]
    (cons username (map user [:score :deaths :proposed-count]))))

(defpage "/leaderboard" []
         (println "leaderboard")
         (response/json
           (map user-line @leaderboard)))

(defpage "/pirate/:name" {:keys [name]}
         (println (@user-summaries name))
         ;TODO: cheshire required to encode a date... sigh
         (encode (assoc (@user-summaries name) :username name)))

(defpage "/stats/:a/:b/:c/:d/:e" {:keys [a b c d e]}
         (println "stats:" [a b c d e])
         (response/json (@proposal-statistics [a b c d e])))

(defpage "/ping" []
         (if-let [username (session/get :username)]
           (response/json username)
           (response/status 401 "Please login")))

(defpage [:post "/login"] {:keys [username password]}
         (println "login" username)
         (if (= password (get-in @user-summaries [username :password]))
           (response/json (session/put! :username username))
           (response/status 401 "Login failed")))

(defpage [:post "/logout" :get "/logout"] []
         (println "logout")
         (session/clear!)
         (response/json "Logged out"))

