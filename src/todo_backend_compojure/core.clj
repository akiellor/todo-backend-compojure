(ns todo-backend-compojure.core
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]
            [todo-backend-compojure.store :as store]))

(defn parse [body]
  (json/read-str (slurp body) :key-fn keyword))

(defn res->created [result]
  {:status 201
   :headers {"Location" (:url result)}
   :body result})

(defn res->no-content []
  {:status 204})

(defn res->ok [body]
  {:status 200 :body body})

(defroutes core-routes
  (OPTIONS "/todos" []
           {:status 200})
  (OPTIONS "/todos/:id" [id]
           {:status 200})
  (GET "/todos" []
       (res->ok (store/get-all)))
  (GET "/todos/:id" {{id :id} :params}
       (res->ok (store/get-todo id)))
  (POST "/todos" {body :body}
        (-> body
            parse
            store/create-todo
            res->created))
  (PATCH "/todos/:id" {{id :id} :params body :body}
         (-> body
             parse
             (#(store/patch-todo id %))
             res->ok))
  (DELETE "/todos" []
          (store/delete-all)
          (res->no-content))
  (DELETE "/todos/:id" {{id :id} :params}
          (store/delete-todo id)
          (res->no-content)))
