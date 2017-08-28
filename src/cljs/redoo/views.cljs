(ns redoo.views
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [re-com.core :as rc]))

;; Todos

;; Main input box to add todos
;; The widget takes care of managing state
;; The caller (in this case [home-panel] defines what event handlers are dispatched
(defn new-todo-input
  [{:keys [on-save]}]
  (let [val (r/atom "")
        stop #(reset! val "")
        save #(let [v (-> @val str clojure.string/trim)]
                (when (seq v) (on-save v)
                              (stop)))]
    (fn []
      [:input
       {:type        "text"
        :value       @val
        :on-blur     save
        :on-change   #(reset! val (-> % .-target .-value))
        :on-key-down #(case (.-which %)
                        13 (save)
                        27 (stop)
                        nil)}])))

(defn todo-input
  [{:keys [on-save on-stop]}]
  (let [stop on-stop
        save on-save
        itemval (r/atom "foo")]

    (fn []
      [rc/input-text
       {:model       itemval
        :class       "edit"
        :on-change   #(reset! itemval (-> % .-target .-value))
        :on-blur     (save)
        :on-key-down #(case (.-which %)
                        13 (save)
                        27 (stop)
                        nil)}])))

(defn todo-item
  [{:keys [id title status]}]
  (let [checked? (r/atom (case status
                           :active false
                           :done true
                           :waiting false))
        editing (r/atom false)
        itemval (r/atom "")]
    (fn [{:keys [id title status]}]
      [rc/h-box
       :gap "1em"
       :align :center
       :children [[rc/checkbox
                   :model checked?
                   :on-change #(dispatch [:toggle-todo-done id])]
                  [:label
                   {:on-double-click #(reset! editing true)
                    :class           (str "itemtitle " (case status :done "done " :waiting "waiting " :active "active "))}
                   title]
                  (when @editing
                    [:todo-input
                     :on-save #(dispatch [:update-todo-title id %])
                     :on-stop #(reset! editing false)])
                  [rc/gap
                   :size "auto"]
                  [rc/button
                   :label "delete"
                   :on-click #(dispatch [:delete-todo id])]
                  ]])))


(defn task-list
  []
  (let [visible-todos @(subscribe [:visible-todos])
        all-complete? @(subscribe [:all-complete?])]
    [rc/v-box
     :class "todo-list"
     :size "auto"
     :children [
                (for [todo visible-todos]
                  ^{:key (:id todo)} [todo-item todo])]]))

;; home

(defn home-title []
  (let [app-name (subscribe [:app-name])]
    (fn []
      [rc/v-box
       :children [
                  [rc/title
                   :label (str @app-name)
                   :level :level1]]])))


(defn link-to-about-page []
  [rc/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn home-panel []
  [rc/h-box
   :justify :center
   :children [[rc/gap
               :size "20px"]
              [rc/v-box
               :size "0 1 700px"
               :gap "1em"
               :children [[home-title]
                          [new-todo-input
                           {:on-save #(dispatch [:add-todo %])}]
                          [task-list]]]
              [rc/gap
               :size "20px"]]])

;; about

(defn about-title []
  [rc/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [rc/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [rc/v-box
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
      [rc/v-box
       :height "100%"
       :children [[panels @active-panel]]])))
