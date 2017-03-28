(ns redoo.db
  (:require [cljs.reader]
            [cljs.spec :as s]
            [re-frame.core :as re-frame]))

(s/def ::id int?)
(s/def ::title string?)
(s/def ::app-name string?)
(s/def ::status #{:not-started :in-progress :waiting :done})
(s/def ::todo (s/keys :req-un [::id ::title ::status]))
(s/def ::todos (s/and
                 (s/map-of ::id ::todo)
                 #(instance? PersistentTreeMap %)))

(s/def ::showing #{:all :active :done})

(s/def ::db (s/keys :req-un [::todos ::showing]))

(def default-value
  {:todos (sorted-map)
   :showing :all
   :active-panel :home-panel
   :app-name "Redoo"})

(def fixtures
  ["Add fixtures to the db"
   "Add text box using re-com to enter data"
   "Add check box to todo items"
   "Mark completed items with strike through"])

;{
; 1 {:id 2 :title "Task 1" :status :not-started}
; 3 {:id 4 :title "Task 2" :status :not-started}
; }

;; -- Local Storage  ----------------------------------------------------------
;;
;; Part of the todomvc challenge is to store todos in LocalStorage, and
;; on app startup, reload the todos from when the program was last run.
;; But the challenge stipulates to NOT  load the setting for the "showing"
;; filter. Just the todos.
;;

(def ls-key "todos-reframe")                          ;; localstore key

(defn todos->local-store
  "Puts todos into localStorage"
  [todos]
  (.setItem js/localStorage ls-key (str todos)))     ;; sorted-map writen as an EDN map

;; register a coeffect handler which will load a value from localstore
;; To see it used look in events.clj at the event handler for `:initialise-db`
(re-frame/reg-cofx
  :local-store-todos
  (fn [cofx _]
    "Read in todos from localstore, and process into a map we can merge into app-db."
    (assoc cofx :local-store-todos
                (into (sorted-map)
                      (some->> (.getItem js/localStorage ls-key)
                               (cljs.reader/read-string)       ;; stored as an EDN map.
                               )))))