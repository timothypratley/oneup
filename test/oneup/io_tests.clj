(ns oneup.io-tests
  (:use [clojure.test]
        [oneup.models.domain]
        [oneup.models.read]
        [oneup.models.io]
        [oneup.models.injector]))

(deftest test-add-user-command
         (add-user-command "bluebeard" "barnacle")
         (is (:password ((@world :user) "bluebeard") "barnacle")))

(deftest test-hydration-domain
         (read-events (partial send world accept)))

(deftest test-hydrate-read
         (read-events denormalize))
