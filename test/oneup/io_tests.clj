(ns oneup.io-tests
  (:use [clojure.test]
        [oneup.models.domain]
        [oneup.models.read]
        [oneup.models.io]
        [oneup.models.injector]))

(deftest test-add-user-command
         (let [username "bluebeard"
               password "barnacle"]
           (add-user-command username password)
           (is (:password (user username) password))
           (is (:joined (@pirate-summaries username)))))

(deftest test-hydrate
         (read-events hydrate))
