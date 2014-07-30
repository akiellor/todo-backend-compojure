(ns todo-backend-compojure.main-test
  (:require [clojure.test :refer :all]
            [todo-backend-compojure.main :refer :all]))

(deftest expand-url-body-test
  (testing "expands first level urls"
    (is (= (expand-url-body "http://localhost" {:url "/path"}) {:url "http://localhost/path"})))
  (testing "expands nested urls"
    (is (= (expand-url-body "http://localhost" [{:url "/path"}]) [{:url "http://localhost/path"}]))))
