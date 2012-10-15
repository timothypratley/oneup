(ns oneup.domain-tests
  (:use [clojure.test]
        [oneup.models.domain]))

(deftest integration-tests
         (testing "When adding a user, their password is copied"
                  (println "ADDING "
                           (add-user-command "redbeard" "foo"))
                  (is (= (:password (user "redbeard")) "foo")))
         (testing "When adding a user, empty username is rejected"
                  (println "ADDING "
                           (add-user-command "" "secret"))
                  (is (nil? (user ""))))

         (testing "When adding a proposal"
                  (println "PROPOSING "
                           (add-proposal-command "redbeard" [4 3 3]))
                  (is (= (get-in (user "redbeard") [:proposal 3 :gold])
                         [4 3 3]))) 
         (testing "When adding a vote"
                  (println "ADDING "
                           (add-user-command "blackbeard" "foo"))
                  (is (= (:password (user "blackbeard")) "foo"))
                  (println "VOTING "
                           (add-vote-command "blackbeard" "redbeard" 3 3 true))
                  ; at this point redbeard has a majority
                  ; but we don't close the proposal until all votes are in
                  (is (get-vote "redbeard" 3 3)))
         (testing "When adding more votes"
                  (println "VOTING "
                           (add-vote-command "blackbeard" "redbeard" 3 2 true))
                  (is (nil? (get-vote "redbeard" 3 2)))
                  (is (not (empty? ((user "redbeard") :proposal 3))))
                  (println "ADDING "
                           (add-user-command "greenbeard" "foo"))
                  (println "VOTING "
                           (add-vote-command "blackbeard" "greenbeard" 3 2 true))
                  (is (nil? (proposal "blackbeard" 3)))))

