(ns oneup.read-tests
  (:use [clojure.test]
        [oneup.models.read]))

(deftest read-tests
         (testing "Stats updated"
                  ; this is a pretty boring test... nothing was accepted
                  ; but if we rehydrate accepted events the score continues up
                  (is (= (score "blackbeard") 0)))
         (testing "Available proposals"
                  ; TODO:
                  (is false)

