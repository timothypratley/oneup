(ns oneup.models.io
  (:use [clojure.java.io]
        [cheshire.core]))

(def filename "event_log.clj")

(defn store
  "Stores an event in a file"
  [event]
  (io! (with-open [w (writer filename :append true)]
                  (generate-stream [(java.util.Date.) event] w))))

(defn read-events [f]
  (io! (with-open [r (reader filename)]
                  (doseq [e (parsed-seq r true)]
                    (println e)
                    (f e)))))
