(ns oneup.helper-tests
  (:use [clojure.test]
        [oneup.models.helper]))

(deftest test-reconcile
         (is (reconcile {:bar 0} {:foo 1}
                        [:copy :foo])
             {:bar 0
              :foo 1})
         (is (reconcile {:bar 0} {:foo 1}
                        [:copy :foo :baz])
             {:bar 0
              :baz 1})
         (is (reconcile {:bar 0} {:foo 1}
                        [:update :bar inc])
             {:bar 1})
         (is (reconcile {:bar 0} {:foo 1}
                        [:update :bar + :foo])))
