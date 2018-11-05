(ns redoo.core
    (:require [dirac.runtime :as dirac]
              [devtools.core :as devtools]
              [reagent.core :as reagent]
              [re-frame.core :as re-frame :refer [dispatch-sync dispatch]]
              [redoo.events]
              [redoo.subs]
              [redoo.routes :as routes]
              [redoo.views :as views]
              [redoo.config :as config]
              [redoo.db :as db]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")
    (devtools/install!)
    (dirac/install!)))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (dispatch-sync [:initialise-db])
  ;; Only insert fixtures when there are no todos in the appdb
  (when (empty? (:todos @re-frame.db/app-db))
    (run! #(dispatch-sync [:add-todo %]) db/fixtures))
  (dev-setup)
  (mount-root))
