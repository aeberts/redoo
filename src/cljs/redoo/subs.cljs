(ns redoo.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub        ;; we can check if there is data
  :initialised?          ;; usage (subscribe [:initialised?])
  (fn  [db _]
    (not (empty? db))))  ;; do we have data

(reg-sub
 :app-config
 (fn [db]
   (:app-config db)))

;(reg-sub
; :active-panel
; (fn [db _]
;   (get-in db [:app-config :active-panel])))

(reg-sub
  :app-name
  (fn [db]
    (:app-name db)))

(reg-sub
  :active-panel
  (fn [db]
    (:active-panel db)))

(reg-sub
  :tasks
  (fn [db]
    (:tasks db)))

(reg-sub
  :showing
  (fn [db _]        ;; db is the (map) value in app-db
    (:showing db)))

;; Next, the registration of a similar handler is done in two steps.
;; First, we `defn` a pure handler function.  Then, we use `reg-sub` to register it.
;; Two steps. This is different to that first registration, above, which was done in one step.
(defn sorted-todos
  [db _]
  (:todos db))
(reg-sub :sorted-todos sorted-todos)

(reg-sub
  :todos
  (fn [query-v _]
    (subscribe [:sorted-todos]))
  (fn [sorted-todos query-v _]
    (vals sorted-todos)))

(reg-sub
  :visible-todos
  (fn [query-v _]
    [(subscribe [:todos])
     (subscribe [:showing])])

  ;; computation function
  (fn [[todos showing] _]   ;; that 1st parameter is a 2-vector of values
    (let [filter-fn (case showing
                      :active (complement :done)
                      :done   :done
                      :all    identity)]
      (filter filter-fn todos))))

(reg-sub
  :all-complete?
  :<- [:todos]
  (fn [todos _]
    (seq todos)))
