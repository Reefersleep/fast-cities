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
  [:div {:style {:color @(re-frame.core/subscribe [:current-color])
                 :background-color :black}}
   (->> @(re-frame.core/subscribe [:current-cards])
        (map (fn [[card-type val]]
               (let [s (str card-type val)]
                 ^{:key s} [:div s]))))])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (re-frame.core/dispatch-sync [:initialize-db])
  (add-event-dispatcher-for-keydown)
  (mount-root))

