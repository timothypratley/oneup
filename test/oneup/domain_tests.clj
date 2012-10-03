(ns oneup.domain-tests
  (:use [clojure.test]
        [oneup.models.domain]))

(deftest integration-tests
         (testing "When adding a user, their password is copied"
                  (println "ADDING "
                           (add-user-command "redbeard" "foo"))
                  (is (:password (user "redbeard")) "foo"))
         (testing "When adding a user, empty username is rejected"
                  (is (string? (add-user-command "" "secret")))
                  (is (nil? (user ""))))

         (testing "When adding a proposal"
                  (println "PROPOSING "
                           (add-proposal-command "redbeard" [2 2 2 2 2]))
                  (is (proposal "redbeard" 5) {1 {:gold 2}
                                               2 {:gold 2}
                                               3 {:gold 2}
                                               4 {:gold 2}
                                               5 {:gold 2}}))

         (testing "When adding a vote"
                  (println "VOTING "
                           (add-vote-command "blackbeard" "redbeard" 5 3 true))
                  (is (get-vote "blackbeard" 5 3) true)))

