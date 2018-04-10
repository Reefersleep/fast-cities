(ns fast-cities.events
  (:require [re-frame.core]
            [fast-cities.db]))

(defn positions
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                coll))

(def colors [:yellow :blue :white :green :red])

(def card-values [:handshake-1
                  :handshake-2
                  :handshake-3
                  2
                  3
                  4
                  5
                  6
                  7
                  8
                  9
                  10])

(def initialized-cards
  (->> card-values
       (map (fn [v]
              [v false]))
       (into {})))

(def initialized-colors
  (->> colors
       (map (fn [color]
              [color initialized-cards]))
       (into {})))

(def keycode->action {65  :remove-handshake
                      81  :add-handshake
                      83  2
                      68  3
                      70  4
                      71  5
                      72  6
                      74  7
                      75  8
                      76  9
                      186 10
                      13  :next-colour
                      32  :next-colour
                      8   :previous-colour})

(re-frame.core/reg-event-fx
 :initialize-db
 (fn [_]
   {:db {:cards         initialized-colors
         :colors        colors
         :current-color :white}}))

(defn shift-colors-> [colors]
  (take 5 (drop 1 (cycle colors))))

(defn shift-colors-< [colors]
  (take 5 (drop 4 (cycle colors))))

(defn left-color-of [colors current-color]
  (let [idx (->> colors
                 (positions #{current-color})
                 first)]
    (if (= 0 idx)
      (last colors)
      (get colors (- idx 1)))))

(defn right-color-of [colors current-color]
  (let [idx (->> colors
                 (positions #{current-color})
                 first)
        max-idx (-> colors
                    count
                    (- 1))]
    (if (= max-idx idx)
      (first colors)
      (get colors (+ idx 1)))))

(defn add-handshake [_ color db]
  (let [handshake-1 (get-in db [:cards color :handshake-1])
        handshake-2 (get-in db [:cards color :handshake-2])
        handshake-3 (get-in db [:cards color :handshake-3])]
    (cond-> db
      (not handshake-1) (assoc-in [:cards color :handshake-1] true)
      handshake-1       (assoc-in [:cards color :handshake-2] true)
      handshake-2       (assoc-in [:cards color :handshake-3] true))))

(defn remove-handshake [_ color db]
  (let [handshake-1 (get-in db [:cards color :handshake-1])
        handshake-2 (get-in db [:cards color :handshake-2])
        handshake-3 (get-in db [:cards color :handshake-3])] 
    (cond-> db
      handshake-3       (assoc-in [:cards color :handshake-3] false)
      (not handshake-3) (assoc-in [:cards color :handshake-2] false)
      (not handshake-2) (assoc-in [:cards color :handshake-1] false))))

(defn toggle-number-card [number color db]
  (update-in db [:cards color number] not))

(defn toggle-card [db current-color action]
  (let [card-type (case action
                    :add-handshake    :handshake
                    :remove-handshake :handshake
                    action)
        update-fn (case action
                    :add-handshake    add-handshake
                    :remove-handshake remove-handshake
                    toggle-number-card)]
    (update-fn action current-color db)))

(re-frame.core/reg-event-db
 :enter-keycode
 (fn [db [_ keycode]]
   (let [action (get keycode->action keycode)
         current-color (:current-color db)
         colors        (:colors db)]
     (case action
       nil              db
       :next-colour     (update db :current-color (partial right-color-of colors))
       :previous-colour (update db :current-color (partial left-color-of colors))
       (toggle-card db current-color action)))))

(re-frame.core/reg-event-db
 :mouse-toggle-card
 (fn [db [_ color-identity card-identity]]
   (-> db
       (update-in [:cards color-identity card-identity] not)
       (assoc :current-color color-identity))))

(re-frame.core/reg-event-db
 :mousing-over
 (fn [db [_ color-identity card-identity]]
   (assoc-in db [:mouseover color-identity card-identity] true)))

(re-frame.core/reg-event-db
 :mouse-leave
 (fn [db [_ color-identity card-identity]]
   (assoc-in db [:mouseover color-identity card-identity] nil)))
