(ns fast-cities.subs
  (:require [re-frame.core]
            [fast-cities.db]))

(re-frame.core/reg-sub
 :current-color
 fast-cities.db/current-color)

(re-frame.core/reg-sub
 :colors
 (fn [db _]
   (:colors db)))

(re-frame.core/reg-sub
 :current-cards
 (fn [db _]
   (let [current-color (fast-cities.db/current-color db)]
     (->> (get-in db [:cards current-color])
          (sort-by (fn [[card-type _]]
                     (case card-type
                       :handshake-1 -1
                       :handshake-2 0
                       :handshake-3 1
                       card-type)))))))
