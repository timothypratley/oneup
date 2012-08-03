(ns oneup.io-tests
  (:use [clojure.test]
        [oneup.models.domain]))

(deftest test-raise
         (raise {:type :user-added
                 :username "bluebeard"
                 :password "barnacle"}))

(deftest test-reconcile
         (is (reconcile {:bar 0} {:foo 1}
                    [:copy :foo])
             {:bar 0
              :foo 1})
         (is (reconcile {} {:foo 1}
                    [:copy :foo])
             {:foo 1}))
