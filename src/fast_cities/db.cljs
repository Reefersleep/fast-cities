(ns fast-cities.db)

;; Card sorting

(defn sort-value-for-card-type
  "Returns the corresponding integer
  sort value for card-type."
  [card-type]
  (case card-type
    :handshake-1 -1
    :handshake-2 0
    :handshake-3 1
    card-type))

(defn sort-stack [cards]
  (->> cards
       (into (sorted-map-by (fn [key1 key2]
                              (compare (sort-value-for-card-type key1)
                                       (sort-value-for-card-type key2)))))))

(defn sort-ascending
  "Sorts cards of each color ascending,
  starting with the three handshake cards
  and progressing upwards from 2 through 10."
  [cards]
  (->> cards
       (map (fn [[color cards]]
              [color (sort-stack cards)]))
       (into {})))

(def default-keydown-rules
  {:event-keys (->> (range 48 58)       ;; which-codes for numbers 1 through 0
                    (into (list 13 32)) ;; which-codes for <Enter> and <Space>
                    (map #(vector {:which %}))
                    (into [[:enter-which-code]])
                    vector)})
