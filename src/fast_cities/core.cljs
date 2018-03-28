(ns fast-cities.core
    (:require
     [reagent.core :as r]
     [re-frame.core]
     [fast-cities.subs]
     [fast-cities.events]))

;; -------------------------
;; Views

(defn add-event-dispatcher-for-keydown []
  (.addEventListener js/document
                     "keydown"
                     (fn [event]
                       (let [keycode (.-keyCode event)]
                         (re-frame.core/dispatch [:enter-keycode keycode])))))

(defn home-page []
  [:div
   [:div {:style {:display        :flex
                  :flex-direction :row
                  :border         "1px solid black"}}
    (->> @(re-frame.core/subscribe [:colors])
         (map (fn [color]
                ^{:key color} [:div {:style {:background-color color
                                             :display          :flex
                                             :min-height       10
                                             :flex             "1 0 auto"}}])))]
   [:div {:style {:color            @(re-frame.core/subscribe [:current-color])
                  :background-color :white}}
    (->> @(re-frame.core/subscribe [:current-cards])
         (map (fn [[card-type val]]
                (let [s (str (case card-type
                               :handshake-1 "handshake"
                               :handshake-2 "handshake"
                               :handshake-3 "handshake"
                               card-type))]
                  ^{:key card-type}
                  [:div {:style {:border-top-left-radius  25
                                 :border-top-right-radius 25
                                 :padding-left            20
                                 :line-height             "2em"
                                 :color                   @(re-frame.core/subscribe [:current-color])
                                 :opacity                 (if val
                                                            1
                                                            0.1)
                                 :background-color        "rgba(0,0,0,1)"}}
                   s]))))]])
[]
;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (re-frame.core/dispatch-sync [:initialize-db])
  (add-event-dispatcher-for-keydown)
  (mount-root))

