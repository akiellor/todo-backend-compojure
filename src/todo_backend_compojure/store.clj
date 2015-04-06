(ns todo-backend-compojure.store)

(def todos (atom {}))

(defn uuid [] (str (java.util.UUID/randomUUID)))

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

