(ns oneup.views.commands
  (:require [noir.session :as session])
  (:use [noir.core]
        [noir.validation]
        [noir.response :only [json]]
        [oneup.models.domain]
        [oneup.models.read]
        [oneup.models.io]
        [oneup.models.injector]))


;Start or join a pirate group
(defpage [:post "/plunder"] []
         (println "plunder"))

;As leader propose how to divide the gold
(defpage [:post "/propose/:a/:b/:c/:d/:e"] {:keys [a b c d e]}
         (println "propose" a b c d e)
         (add-proposal-command
           (session/get :username)
           (map #(Integer/parseInt %) [a b c d e])))

;As subordinate vote yes or no on a proposal
(defn yes [vote] (boolean (#{\y \Y} (first vote))))
(defn no [vote] (boolean (#{\n \N} (first vote))))
(defpage [:post "/vote/:vote"] {:keys [vote]}
         (println "vote" vote)
         ;TODO: failed validation returns 404 without this error message...
         (rule (or (yes vote) (no vote)) [:vote "yes or no"])
         (if (not (errors? :vote))
           (add-vote-command (session/get :username) (yes vote)))
         "hi")

(defpage [:post "/login"] {:keys [username password]}
         (println "login" username password)
         (if (add-user-command username password)
           (session/put! :username username)
           ))

(defpage [:post "/logout"] []
         (println "logout")
         (session/clear!))