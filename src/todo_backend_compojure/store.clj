(ns todo-backend-compojure.store
  (:require [clojure.java.jdbc :as j]))

(def db (System/getenv "DATABASE_URL"))

(defn as-todo [row]
  (dissoc (assoc row :order (:sequence row)) :sequence))

(defn as-row [todo]
  (dissoc (assoc todo :sequence (:order todo)) :order))

(defn get-todo [id]
  (println id)
  (first (j/query db
                  ["select * from todos where id = ?::uuid" id]
                  :row-fn as-todo)))

(defn create-todo [todo]
  (as-todo (first (j/insert! db :todos
                             (as-row (merge todo {:completed false}))))))

(defn get-all []
  (j/query db ["select * from todos"]
           :row-fn as-todo))

(defn patch-todo [id body]
  (j/update! db :todos (as-row body) ["id = ?::uuid" id])
  (get-todo id))

(defn delete-all []
  (j/delete! db :todos [true]))

(defn delete-todo [id]
  (j/delete! db :todos ["id = ?::uuid" id]))

