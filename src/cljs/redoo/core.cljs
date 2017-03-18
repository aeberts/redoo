(ns redoo.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame :refer [dispatch-sync]]
              [re-frisk.core :refer [enable-re-frisk!]]
              [redoo.events]
              [redoo.subs]
              [redoo.routes :as routes]
              [redoo.views :as views]
              [redoo.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (dispatch-sync [:initialise-db])
  (dev-setup)
  (mount-root))
