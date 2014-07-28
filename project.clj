(defproject todo-backend-compojure "0.1.0-SNAPSHOT"
  :description "a compojure implementation of https://github.com/moredip/todo-backend"
  :url "https://github.com/moredip/todo-backend"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main "todo-backend-compojure.main"
  :uberjar-name "todo-backend-compojure-standalone.jar"
  :aot :all
  :java-target "1.7"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.4"]
                 [compojure "1.1.6"]
                 [ring-server "0.3.1"]
                 [ring/ring-json "0.3.0"]
                 [log4j "1.2.17"]])
