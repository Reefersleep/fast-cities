(ns fast-cities.subs
  (:require [re-frame.core]
            [fast-cities.db]))

(re-frame.core/reg-sub
 :current-color
 fast-cities.db/current-color)

(re-frame.core/reg-sub
 :current-cards
 (fn [db _]
   (let [current-color (fast-cities.db/current-color db)]
     (->> (get-in db [:cards current-color])
          (filter (fn [[card-type selected?]]
                    selected?))))))
