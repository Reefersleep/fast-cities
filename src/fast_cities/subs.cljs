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

(defn score-for-one-color
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
        more-than-8-cards-bonus    (when (<= 8 (count selected-cards))
                                     20)]
    (if (seq selected-cards)
      (-> -20
          (+ accummulated-number-values)
          (* (inc number-of-handshake-cards))
          (+ more-than-8-cards-bonus))
      0)))

(defn sort-value-for-card-type [card-type]
  (case card-type
    :handshake-1 -1
    :handshake-2 0
    :handshake-3 1
    card-type))

(re-frame.core/reg-sub
 :cards
 (fn [db _]
   (->> db
        :cards
        (map (fn [[color cards]]
               [color (->> cards
                           (into (sorted-map-by (fn [key1 key2]
                                                  (compare (sort-value-for-card-type key1)
                                                           (sort-value-for-card-type key2))))))]))
        (into {}))))

(re-frame.core/reg-sub
 :score
 :<- [:cards]
 (fn [cards _]
   (->> cards
        (map score-for-one-color)
        (apply +))))

(re-frame.core/reg-sub
 :score-for-color
 :<- [:cards] ;; this is rendered every time a card is toggled for _all_ colors. not so nice
 (fn [cards [_ color]]
   (-> cards
       (select-keys [color])
       ((fn [color-map]
          [(first color-map) (second color-map)]))
       first ;; huh? Why is it wrapped in a second vector?
       (score-for-one-color))))
