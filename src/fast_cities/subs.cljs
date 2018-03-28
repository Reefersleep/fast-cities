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

(re-frame.core/reg-sub
 :score
 (fn [db _]
   (let [{:keys [cards]} db]
     (->> cards
          (map (fn score-for-one-color
                 [[color cards]]
                 (let [selected-cards             (->> cards
                                                       (filter (fn [[card-type selected?]]
                                                                 selected?)))
                       number-of-handshake-cards  (->> selected-cards
                                                       (filter #(-> %
                                                                    first
                                                                    #{:handshake-1
                                                                      :handshake-2
                                                                      :handshake-3}))
                                                       count)
                       accummulated-number-values (->> selected-cards
                                                       (remove #(-> %
                                                                    first
                                                                    #{:handshake-1
                                                                      :handshake-2
                                                                      :handshake-3}))
                                                       (map first)
                                                       (apply +))
                       more-than-8-cards-bonus    (when (< 8 (count selected-cards))
                                                    20)]
                   (if (seq selected-cards)
                     (-> -20
                         (+ accummulated-number-values)
                         (* (inc number-of-handshake-cards))
                         (+ more-than-8-cards-bonus))
                     0))))
          (apply +)))))
