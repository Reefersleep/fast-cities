(ns fast-cities.db)

(defn current-color [db]
  (-> db
      :colors
      (nth 2)))
