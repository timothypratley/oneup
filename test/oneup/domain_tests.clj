(ns oneup.io-tests
  (:use [clojure.test]
        [oneup.models.domain]))

(deftest test-raise
         (raise {:type :user-added
                 :username "bluebeard"
                 :password "barnacle"}))
