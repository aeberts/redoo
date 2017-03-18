(ns redoo.views
    (:require [re-frame.core :as re-frame :refer [subscribe]]
              [re-com.core :as re-com]))

;; Todos

(defn todo-item
  []
  (fn [{:keys [id status title]}]
    [:li {:class (str (when status "completed "))}
     title]))

(defn task-list
  []
  (let [visible-todos @(subscribe [:visible-todos])
        all-complete? @(subscribe [:all-complete?])]
    [:ul#todo-list
     (for [todo visible-todos]
       ^{:key (:id todo)} [todo-item todo])]))

;; home

(defn home-title []
  (let [app-name (subscribe [:app-name])]
    (fn []
      [re-com/v-box
       :gap "1em"
       :children [
                  [re-com/title
                   :label (str "Hello from " @app-name ". This is the Home Page.")
                   :level :level1]
                  [task-list]
                  ]])))

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title] [link-to-about-page]]])

;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [[panels @active-panel]]])))
