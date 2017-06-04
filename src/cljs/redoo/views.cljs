(ns redoo.views
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [re-com.core :as re-com]))

;; Todos

;; Main input box to add todos
;; The widget takes care of managing state
;; The caller (in this case [home-panel] defines what event handlers are dispatched
(defn todo-input
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

(defn todo-item
  [{:keys [todo on-save]}]
  (let [{:keys [id title status]} todo
        checkval (r/atom "")
        editing (r/atom false)
        itemval (r/atom "")
        stop #(swap! editing "false")
        save #(let [v (-> @itemval str clojure.string/trim)]
                (when (seq v) (on-save v)
                              (stop)))
        ]
    (fn []
      [re-com/h-box
       :gap "1em"
       :align :center
       :children [
                  [:input.toggle
                   {:type     "checkbox"
                    :value    @checkval
                    :on-click #(dispatch [:toggle-todo-done id])}]
                  (when @editing
                    ([:input
                      {:type        "text"
                       :value       title
                       :on-change   #(reset! itemval (-> % .-target .-value))
                       :on-blur     (save)
                       :on-key-down #(case (.-which %)
                                       13 (save)
                                       27 (stop)
                                       nil)}]))
                  [:li
                   {:class    (str (case status :active "active " :waiting "waiting " :done "done "))
                    :on-double-click #(reset! editing true)}
                   title]
                  [re-com/gap
                   :size "1"]
                  [re-com/button
                   :label "delete"
                   :on-click #(dispatch [:delete-todo id])]
                  ]])))


(defn task-list
  []
  (let [visible-todos @(subscribe [:visible-todos])
        all-complete? @(subscribe [:all-complete?])]
    [:ul#todo-list
     (for [todo visible-todos]
       ^{:key (:id todo)} [todo-item {:todo todo :on-save #(dispatch [:update-todo-title %])}])]))

;; home

(defn home-title []
  (let [app-name (subscribe [:app-name])]
    (fn []
      [re-com/v-box
       :children [
                  [re-com/title
                   :label (str @app-name)
                   :level :level1]]])))


(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn home-panel []
  [re-com/h-box
   :justify :center
   :children [[re-com/gap
               :size "20px"]
              [re-com/v-box
               :size "0 1 700px"
               :gap "1em"
               :children [[home-title]
                          [todo-input
                           {:on-save #(dispatch [:add-todo %])}]
                          [task-list]]]
              [re-com/gap
               :size "20px"]]])

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
