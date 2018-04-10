(ns fast-cities.subs
  (:require [re-frame.core]
            [fast-cities.db]))

(re-frame.core/reg-sub
 :current-color
 :current-color)

(re-frame.core/reg-sub
 :colors
 (fn [db _]
   (:colors db)))

(re-frame.core/reg-sub
 :current-cards
 (fn [db _]
   (let [current-color (:current-color db)]
     (->> (get-in db [:cards current-color])
          (sort-by (fn [[card-type _]]
                     (case card-type
                       :handshake-1 -1
                       :handshake-2 0
                       :handshake-3 1
                       card-type)))))))

(defn score-for-one-color
  [cards]
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

(re-frame.core/reg-sub
 :cards
 (fn [db _]
   (->> db
        :cards)))

(re-frame.core/reg-sub
 :score
 :<- [:cards]
 (fn [cards _]
   (->> cards
        (map (fn [[color-identity cards]]
               (score-for-one-color cards)))
        (apply +))))

(re-frame.core/reg-sub
 :score-for-color
 (fn [db [_ color-identity]]
   (let [{:keys [:cards :show-score-details?]} db]
     (when show-score-details?
       (-> db
           :cards
           (get color-identity)
           score-for-one-color)))))

(re-frame.core/reg-sub
 :mouse-over?
 (fn [db [_ color-identity card-identity]] ;; TODO ineffective - subscribe to something that doesn't change as often first
   (get-in db [:mouseover color-identity card-identity])))

(re-frame.core/reg-sub
 :show-indicator?
 :<- [:current-color]
 (fn [current-color [_ color-identity]]
   (= current-color color-identity)))
