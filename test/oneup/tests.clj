(ns oneup.tests
  (:use [clojure.test]
        [oneup.models.domain]
        [oneup.models.read]
        [oneup.models.io]
        [oneup.models.injector]))

;(deftest test-raise
         ;(raise {:type :user-added
                 ;:username "bluebeard"
                 ;:password "barnacle"}))

(deftest test-add-user-command
         (add-user-command "bluebeard" "barnacle")
         (is (:password ((@world :user) "bluebeard") "barnacle")))

(deftest test-hydration
         (read-events (partial send world accept)))
