(ns oneup.io-tests
  (:use [clojure.test]
        [oneup.models.domain]
        [oneup.models.read]
        [oneup.models.io]
        [oneup.models.injector]))

#_(deftest test-add-user-command
         (let [username "bluebeard"
               password "barnacle"]
           (add-user-command username password)
           (is (:password (user username) password))
           (is (:joined (@users username)))))

; this gets run after we already created the events,
; mayhem ensues
#_(deftest test-hydrate
         (read-events hydrate))
