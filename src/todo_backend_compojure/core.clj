(ns todo-backend-compojure.core
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]))

(def todos (atom {}))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn parse [body]
  (json/read-str (slurp body) :key-fn keyword))

(defn get-todo [id]
  (get-in @todos [id]))

(defn create-todo [todo]
  (let [id (uuid) new-todo (merge todo {:id id :completed false :url (str "/todos/" id)})]
    (swap! todos (fn [state]
                     (assoc state id new-todo)))
    (get-todo id)))

(defn get-all []
  (let [ids (keys @todos)]
    (map get-todo ids)))

(defn patch-todo [id body]
  (swap! todos (fn [state]
                 (merge state {id (merge (get-todo id) body)})))
  (get-todo id))

(defn delete-all []
  (swap! todos {}))

(defn delete-todo [id]
  (swap! todos #(dissoc % id)))

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
       (res->ok (get-all)))
  (GET "/todos/:id" {{id :id} :params}
       (res->ok (get-todo id)))
  (POST "/todos" {body :body}
        (-> body
            parse
            create-todo
            res->created))
  (PATCH "/todos/:id" {{id :id} :params body :body}
         (-> body
             parse
             (#(patch-todo id %))
             res->ok))
  (DELETE "/todos" []
          (delete-all)
          (res->no-content))
  (DELETE "/todos/:id" {{id :id} :params}
          (delete-todo id)
          (res->no-content)))
