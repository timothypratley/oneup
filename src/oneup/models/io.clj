(ns oneup.models.io
  (:use [clojure.java.io]
        [cheshire.core]
        [clj-time.format :only [parse]]))

(def events (file "events.json"))

(defn store
  "Stores an event in a file"
  [event]
  (io! (with-open [w (writer events :append true)]
                  (generate-stream event w)
                  (.newLine w))
       event))

(defn custom-fields [k v]
  (cond
    (= k :type) [k (keyword v)]
    (= k :when) [k (parse v)]
    :else [k v]))
(defn update-fields [e]
  (into {}
        (for [[k v] e]
             (custom-fields k v))))
(defn read-events [f]
  ; TODO: handle file not found
  (io! (when (.exists events)
         (with-open [r (reader events)]
           (doseq [e (parsed-seq r true)]
             (f (update-fields e)))))))
