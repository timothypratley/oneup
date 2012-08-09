(defproject oneup "0.1.0-SNAPSHOT"
            :description "A website written in noir using AngularJS"
            :main oneup.server
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.2.2"]
                           [cheshire "4.0.1"]
                           [clj-time "0.4.4"]
                           [expectations "1.4.3"]]
            :dev-dependencies [[lein-expectations "0.0.5"]
                               [lein-autoexpect "0.1.2"]])
