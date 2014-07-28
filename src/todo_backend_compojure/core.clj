(ns todo-backend-compojure.core
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]))

(def store (atom {:todos {}}))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn parse [body]
  (json/read-str (slurp body) :key-fn keyword))

(defn get-todo [id]
  (let [todo (get-in @store [:todos id])]
    (if (nil? todo)
      nil 
      (assoc todo :url (str "http://localhost:8080/todos/" id)))))

(defn create [todo]
  (let [id (uuid) new-todo (merge todo {:id id :completed false})]
    (swap! store (fn [state]
                   (let [{todos :todos} state]
                     {:todos (assoc todos id new-todo)})))
    (get-todo id)))

(defn get-all []
  (let [ids (keys (:todos @store))]
    (map get-todo ids)))

(defn patch-todo [id body]
  (swap! store (fn [state]
                 (assoc-in state [:todos id] (merge (get-todo id) body))))
  (get-todo id))

(defn delete []
  (swap! store assoc :todos {}))

(defn delete-todo [id]
  (swap! store (fn [state]
                 {:id (:id state) :todos (dissoc (:todos state) id)})))

(defn res->created [result]
  {:status 201
   :headers {"Location" (str "http://localhost:8080" "/todos/" (:id result))}
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
            create
            res->created))
  (PATCH "/todos/:id" {{id :id} :params body :body}
         (-> body
             parse
             (#(patch-todo id %))
             res->ok))
  (DELETE "/todos" []
          (delete)
          (res->no-content))
  (DELETE "/todos/:id" {{id :id} :params}
          (delete-todo id)
          (res->no-content)))
