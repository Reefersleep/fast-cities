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
