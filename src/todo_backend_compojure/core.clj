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

(defn todo-representation [todo]
  (assoc todo :url (str "/todos/" (:id todo))))

(defroutes core-routes
  (OPTIONS "/todos" []
           {:status 200})
  (OPTIONS "/todos/:id" [id]
           {:status 200})
  (GET "/todos" []
       (-> (store/get-all)
           (#(map todo-representation %))
           res->ok))
  (GET "/todos/:id" {{id :id} :params}
       (-> (store/get-todo id)
           todo-representation
           res->ok))
  (POST "/todos" {body :body}
        (-> body
            parse
            store/create-todo
            todo-representation
            res->created))
  (PATCH "/todos/:id" {{id :id} :params body :body}
         (-> body
             parse
             (#(store/patch-todo id %))
             todo-representation
             res->ok))
  (DELETE "/todos" []
          (store/delete-all)
          (res->no-content))
  (DELETE "/todos/:id" {{id :id} :params}
          (store/delete-todo id)
          (res->no-content)))
