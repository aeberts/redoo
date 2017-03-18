(ns redoo.events
  (:require [re-frame.core :as re-frame :refer [reg-event-db after path trim-v debug reg-event-fx inject-cofx]]
            [redoo.db :as db :refer [default-value todos->local-store]]
            [cljs.spec :as s]))

;; Interceptors

(defn check-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

;; Event handlers change state, that's their job. But what happens if there's
;; a bug which corrupts app state in some subtle way? This interceptor is run after
;; each event handler has finished, and it checks app-db against a spec.  This
;; helps us detect event handler bugs early.
(def check-spec-interceptor (after (partial check-and-throw :redoo.db/db)))

;; this interceptor stores todos into local storage
;; we attach it to each event handler which could update todos
(def ->local-store (after todos->local-store))

;; Each event handler can have its own set of interceptors (middleware)
;; But we use the same set of interceptors for all event habdlers related
;; to manipulating todos.
;; A chain of interceptors is a vector.
(def todo-interceptors [check-spec-interceptor               ;; ensure the spec is still valid
                        (path :todos)                        ;; 1st param to handler will be the value from this path
                        ->local-store                        ;; write todos to localstore
                        (when ^boolean js/goog.DEBUG debug)  ;; look in your browser console for debug logs
                        trim-v])                             ;; removes first (event id) element from the event vec

;; Helpers

(defn allocate-next-id
  "Returns the next todo id.
  Assumes todos are sorted.
  Returns one more than the current largest id."
  [todos]
  ((fnil inc 0) (last (keys todos))))

;; Event Handlers

;; usage:  (dispatch [:initialise-db])
(reg-event-fx                     ;; on app startup, create initial state
  :initialise-db                  ;; event id being handled
  [(inject-cofx :local-store-todos)  ;; obtain todos from localstore
   check-spec-interceptor]                                  ;; after the event handler runs, check that app-db matches the spec
  (fn [{:keys [db local-store-todos]} _]                    ;; the handler being registered
    {:db (assoc default-value :todos local-store-todos)}))  ;; all hail the new state


(reg-event-db
  :add-todo
  todo-interceptors
  (fn [todos [text]]
    (let [id (allocate-next-id todos)]
      (assoc todos id {:id id :title text :status :not-started}))))

(reg-event-db
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc db :active-panel active-panel)))
