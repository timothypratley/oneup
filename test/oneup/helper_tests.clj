(ns oneup.helper-tests
  (:use [clojure.test]
        [oneup.models.helper]))

(deftest test-reconcile
         (testing "copy a property from one map to another"
           (is (= (reconcile {:bar 0} {:foo 1}
                             [:copy :foo])
                  {:bar 0 :foo 1})))

         (testing "set a property in a map"
           (is (= (reconcile {} {}
                             [:set :x 1])
                  {:x 1})))

         (testing "setf uses a function with arguments from the other map"
           (is (= (reconcile {} {:x 1 :y 2}
                             [:setf :z + :x :y])
                  {:z 3})))

         (testing "copy a property to a new name"
           (is (= (reconcile {:bar 0} {:foo 1}
                             [:copy :foo :baz])
                  {:bar 0 :baz 1})))

         (testing "updating a property with a function"
           (is (= (reconcile {:bar 0} {:foo 1}
                             [:update :bar inc])
                  {:bar 1})))

         (testing "updating a property with a function that uses an event property"
           (is (= (reconcile {:bar 3} {:foo 3}
                             [:update :bar + :foo])
                  {:bar 6})))

         (testing "updating a property with a function that uses many event properties"
           (is (= (reconcile {:bar 1} {:a 2 :b 3 :c 4}
                             [:update :bar + :a :b :c])
                  {:bar 10})))
         
         (testing "update and copy"
           (is (= (reconcile {:bar 0} {:foo 1}
                             [:update :bar inc]
                             [:copy :foo])
                  {:foo 1 :bar 1}))))

