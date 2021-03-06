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

(defn initialize-colors [colors]
  (->> colors
       (map (fn [color]
              [color initialized-cards]))
       fast-cities.db/sort-ascending
       (into {})))

(def char-code->action
  {49  :cycle-handshake ;; charcodes for number row
   50  2
   51  3
   52  4
   53  5
   54  6
   55  7
   56  8
   57  9
   48  10
   13  :next-colour ;; Enter in Chrome
   0   :next-colour ;; Enter in Firefox ;; TODO Maybe find a more cross-browser-resilient solution...
   32  :previous-colour
   102 :flip-stacks}) ;; The 'f' key

(re-frame.core/reg-event-fx
 :initialize-db
 (fn [_]
   {:db {:cards               (initialize-colors colors)
         :colors              colors
         :current-color       :white
         :show-score-details? false
         :show-options?       false}}))

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

(defn cycle-handshake [_ color db]
  (let [handshake-1 (get-in db [:cards color :handshake-1])
        handshake-2 (get-in db [:cards color :handshake-2])
        handshake-3 (get-in db [:cards color :handshake-3])]
    (cond
      handshake-3 (update-in db [:cards color] assoc :handshake-3 false
                                                     :handshake-2 false
                                                     :handshake-1 false)
      handshake-2 (assoc-in db [:cards color :handshake-3] true)
      handshake-1 (assoc-in db [:cards color :handshake-2] true)
      :else       (assoc-in db [:cards color :handshake-1] true))))

(defn toggle-card [db current-color action]
  (let [card-type (case action
                    :add-handshake    :handshake
                    :remove-handshake :handshake
                    action)
        update-fn (case action
                    :cycle-handshake  cycle-handshake
                    :add-handshake    add-handshake
                    :remove-handshake remove-handshake
                    toggle-number-card)]
    (update-fn action current-color db)))

(re-frame.core/reg-event-db
 :enter-char-code
 (fn [db [_ char-code]]
   (let [action (get char-code->action char-code)
         current-color (:current-color db)
         colors        (:colors db)]
     (case action
       nil              db
       :next-colour     (update db :current-color (partial right-color-of colors))
       :previous-colour (update db :current-color (partial left-color-of colors))
       :flip-stacks     (update db :stacks-flipped? not)
       (-> (toggle-card db current-color action)
           (update-in [:cards current-color] fast-cities.db/sort-stack))))))

(re-frame.core/reg-event-db
 :mouse-toggle-card
 (fn [db [_ color-identity card-identity]]
   (-> db
       (update-in [:cards color-identity card-identity] not)
       (assoc :current-color color-identity)
       (update-in [:cards color-identity] fast-cities.db/sort-stack))))

(re-frame.core/reg-event-db
 :focus-on
 (fn [db [_ color-identity]]
   (-> db
       (assoc :current-color color-identity))))

(re-frame.core/reg-event-db
 :mousing-over
 (fn [db [_ color-identity card-identity]]
   (-> db
       (assoc-in [:mouseover color-identity card-identity] true)
       (update-in [:cards color-identity] fast-cities.db/sort-stack))))

(re-frame.core/reg-event-db
 :mouse-leave
 (fn [db [_ color-identity card-identity]]
   (-> db
       (assoc-in [:mouseover color-identity card-identity] nil)
       (update-in [:cards color-identity] fast-cities.db/sort-stack))))

(re-frame.core/reg-event-db
 :toggle-score-details
 (fn [db _]
   (-> db
       (update :show-score-details? not))))

(re-frame.core/reg-event-db
 :toggle-options
 (fn [db _]
   (-> db
       (update :show-options? not))))

(re-frame.core/reg-event-db
 :toggle-stack-flip
 (fn [db _]
   (-> db
       (update :stacks-flipped? not))))

(re-frame.core/reg-event-db
 :toggle-grey-expedition
 (fn [db _]
   (let [grey-expedition-added? (->> db
                                     :colors
                                     (some #{:grey}))]
     (-> db
         (update :colors (fn toggle-grey-expedition-color-identity [colors]
                           (if grey-expedition-added?
                             (-> colors
                                 butlast
                                 vec)
                             (into colors [:grey]))))
         (update :cards (fn toggle-grey-expedition-stack [cards]
                           (if grey-expedition-added?
                             (dissoc cards :grey)
                             (merge cards (initialize-colors [:grey])))))))))
