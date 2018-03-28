(ns fast-cities.events
  (:require [re-frame.core]
            [fast-cities.db]))

(def colors [:yellow :blue :white :green :red])

(def card-values [:handshake
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
              [v (if (= :handshake v)
                   0
                   false)]))
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
   {:db {:cards  initialized-colors
         :colors colors}}))

(defn shift-colors-> [colors]
  (take 5 (drop 1 (cycle colors))))

(defn shift-colors-< [colors]
  (take 5 (drop 4 (cycle colors))))


(defn inc-to-3-at-most
  [number]
  (if (= number 3)
    number
    (inc number)))

(defn dec-to-0-at-most
  [number]
  (if (= number 0)
    number
    (dec number)))

(defn toggle-card [db current-color action]
  (let [card-type (case action
                    :add-handshake    :handshake
                    :remove-handshake :handshake
                    action)]
    (update-in db
               [:cards current-color card-type]
               (fn [value]
                 (let [update-fn (case action
                                   :add-handshake    inc-to-3-at-most
                                   :remove-handshake dec-to-0-at-most
                                   not)]
                   (update-fn value))))))

(re-frame.core/reg-event-db
 :enter-keycode
 (fn [db [_ keycode]]
   (let [action (get keycode->action keycode)
         current-color (fast-cities.db/current-color db)]
     (case action
       nil              db
       :next-colour     (update db :colors shift-colors->)
       :previous-colour (update db :colors shift-colors-<)
       (toggle-card db current-color action)))))
