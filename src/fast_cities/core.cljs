(ns fast-cities.core
    (:require
     [reagent.core]
     [re-frame.core]
     [fast-cities.subs]
     [fast-cities.events]))

;; -------------------------
;; Views

(defn add-event-dispatcher-for-keydown []
  (.addEventListener js/document
                     "keypress"
                     (fn [event]
                       (let [char-code (.-charCode event)]
                         (re-frame.core/dispatch [:enter-char-code char-code])))))

(def color-identities->rgb-colors {:white  "rgb(293,242,239)"
                                   :blue   "rgb(18,176,237)"
                                   :red    "rgb(246,60,54)"
                                   :yellow "rgb(250,244,6)"
                                   :green  "rgb(119,185,12)"})

(def color-identities [:yellow :blue :white :green :red])

(defn card-width->card-height [card-width]
  (* card-width 1.63))

(defn height-of-card-top [card-height]
  (/ card-height 9))

(defn vw [number]
  (str number "vw"))

(defn handshake-icon [{:keys [:height :width :fill]}]
  [:svg {:height            height
         :width             width
         :fill              fill
         :version           "1.1"
         :xmlns             "http://www.w3.org/2000/svg"
         :xmlnsXlink        "http://www.w3.org/1999/xlink"
         :x                 "0px"
         :y                 "0px"
         :viewBox           "0 0 1000 1000"
         :enable-background "new 0 0 1000 1000"
         :xmlSpace          "preserve"}
   [:g
    [:g {:transform "translate(0.000000,511.000000) scale(0.100000,-0.100000)"}
     [:path {:d "M1604.6,3065.9C1148.6,2266.8,108.1,400.4,100.4,367.9c-5.8-34.5,46-72.8,360.2-266.4C682.9-32.6,845.8-120.8,868.8-116.9c28.7,3.8,226.1,339.2,831.6,1414.2c435,774.2,789.5,1421.8,787.6,1435.2c-5.7,30.7-730.1,481-772.2,481C1702.4,3213.4,1652.5,3146.4,1604.6,3065.9z"}]
     [:path {:d "M7886,2985.4c-285.5-176.3-364.1-233.8-362.2-264.4c1.9-51.7,1559.8-2820.7,1598.1-2841.7c36.4-19.2,760.7,413.9,778,465.6c15.3,49.8-1567.5,2857.1-1615.4,2862.8C8265.4,3209.6,8087.2,3110,7886,2985.4z"}]
     [:path {:d "M4900.5,2425.9c-298.9-24.9-605.5-74.7-764.6-124.6c-118.8-38.3-159-61.3-212.7-122.6c-109.2-128.4-557.6-764.6-684.1-971.5c-113.1-184-120.7-205-122.6-316.2c-1.9-113.1,0-120.7,53.7-145.6c69-34.5,291.3-5.8,450.3,59.4c228,92,519.3,335.3,672.6,565.3l67.1,99.6l237.6,76.7c130.3,42.2,254.9,76.6,275.9,76.6c67.1,0,873.8-435,1316.4-710.9c429.2-266.4,1211-824,1655.6-1182.3l226.1-184l80.5,44.1C8278.8-337.3,8411-230,8583.5-57.5l160.9,162.9l-609.4,1078.8c-333.4,594-613.2,1086.5-620.8,1096.1c-7.7,7.7-86.2-5.7-174.4-26.8c-216.5-55.6-394.7-42.2-824,55.6c-496.3,115-689.8,138-1084.6,136.1C5241.6,2443.1,5004,2435.4,4900.5,2425.9z"}]
     [:path {:d "M1855.6,1161.2L1229.1,47.8L1328.7-69c55.6-65.2,141.8-172.5,193.5-237.6c51.7-67.1,95.8-120.7,99.6-120.7c3.8,0,46,28.7,92,65.2c256.8,195.4,630.4,88.1,791.4-226.1l46-92l86.2,116.9c111.1,151.4,224.2,212.7,396.6,212.7c304.7,0,553.8-249.1,557.6-559.5l1.9-130.3h70.9c109.2,0,298.9-107.3,383.2-220.4c84.3-109.2,138-266.4,126.5-373.7l-5.7-72.8l86.2-13.4c195.5-28.7,364.1-147.5,452.2-321.9c92-180.1,84.3-373.7-23-532.7c-36.4-55.6-59.4-105.4-51.7-113.1c7.7-7.7,70.9-32.6,139.9-55.6c454.1-145.6,827.8-178.2,1009.8-88.1c97.7,49.8,281.7,224.2,270.2,258.7c-3.8,11.5-220.4,122.6-484.8,251c-262.5,128.4-484.8,241.5-492.5,254.9c-28.7,44.1-15.3,107.3,32.6,138c46,30.7,59.4,24.9,569.1-222.3c551.9-268.3,615.1-289.3,810.6-260.6c120.7,19.2,210.8,101.6,249.1,228c53.7,180.1,92,145.6-578.7,532.7c-335.3,193.5-624.7,367.9-641.9,387.1c-19.2,17.3-34.5,46-34.5,61.3c0,44.1,76.7,109.2,115,99.6c19.2-5.8,320-174.4,668.8-375.6c626.6-362.1,632.4-366,745.4-366c143.7,0,226.1,28.7,306.6,103.5c74.7,69,105.4,159,95.8,285.5l-5.7,93.9l-728.2,438.8c-400.5,241.5-739.7,456.1-755,477.1c-21.1,30.6-21.1,49.8-5.7,86.2c44.1,95.8,93.9,72.8,917.9-423.5l781.8-469.5l143.7,5.7c113.1,3.8,157.1,15.3,206.9,49.8c141.8,103.5,182,316.2,84.3,461.8c-63.2,93.9-293.2,285.5-799.1,663C6435.4,538.4,5841.4,915.9,5044.2,1326l-178.2,92l-178.2-53.7l-180.1-53.7l-130.3-162.9c-320-398.6-760.7-634.3-1149.7-613.2c-149.5,7.7-206.9,34.5-283.6,138c-32.6,42.2-40.2,78.6-38.3,189.7c1.9,203.1,74.7,346.8,423.5,843.1c88.1,124.6,159,233.8,159,239.5c0,7.7-105.4,30.7-233.8,49.8c-306.6,46-454.1,92-624.7,195.5c-76.6,46-141.8,84.3-143.7,84.3C2484.2,2274.5,2200.6,1774.4,1855.6,1161.2z"}]
     [:path {:d "M1874.8-494.4c-65.1-36.4-300.8-358.3-325.8-442.6c-47.9-178.2,111.1-400.5,314.3-436.9c124.6-23,189.7,23,350.7,237.6c132.2,180.1,143.7,201.2,143.7,287.4c0,118.8-24.9,185.9-101.6,266.4C2148.8-469.5,1989.8-433.1,1874.8-494.4z"}]
     [:path {:d "M2921.1-569.2c-42.2-19.2-157.1-161-400.5-492.5c-187.8-254.9-352.6-486.7-366-513.6c-93.9-180.1,78.6-456.1,297-477.1c164.8-15.3,187.8,5.8,475.2,396.7c484.8,659.2,465.6,626.6,465.6,728.2C3392.5-680.3,3124.2-475.3,2921.1-569.2z"}]
     [:path {:d "M3497.8-1260.9c-55.6-28.7-565.3-699.4-617-812.5c-86.2-189.7,80.5-456.1,298.9-477.1c161-15.3,185.9,5.7,448.4,366c350.7,482.9,339.2,463.7,339.2,576.8c0,122.6-57.5,230-162.9,302.8C3708.6-1237.9,3578.3-1218.8,3497.8-1260.9z"}]
     [:path {:d "M4076.5-1956.5c-69-42.2-410.1-509.7-436.9-597.9c-42.2-138,55.6-321.9,214.6-404.3c90.1-46,182-46,260.6-1.9c74.7,40.3,419.6,500.1,448.4,597.9c28.7,95.8-5.8,212.7-93.9,310.4C4354.4-1923.9,4191.5-1883.7,4076.5-1956.5z"}]]]])

(defn card-symbol [{:keys [:card-identity :class :height :width :fill]}]
  (let [formatted-val (cond
                        (#{:handshake-1 :handshake-2 :handshake-3} card-identity)
                        [handshake-icon {:height (vw height)
                                         :width  (vw width)
                                         :fill   fill}]
                        (#{6 9} card-identity)
                        (str card-identity ".")
                        :else
                        card-identity)]
    [:div {:class class
           :style {:height      (vw height)
                   :width       (vw width)
                   :font-size   (vw height)
                   :line-height (vw height)
                   :text-align  :center}}
     formatted-val]))

(defn resizable-card
  "A component for a Lost Cities playing card.
  This component is able to resize dynamically while
  keeping its dimensions intact."
  [{:keys [card-width-in-vw card-identity color-identity selected?]}]
  (let [color                              (get color-identities->rgb-colors color-identity)
        card-width                         card-width-in-vw
        card-height                        (card-width->card-height card-width-in-vw)
        top-height                         (height-of-card-top card-height)
        bottom-height                      top-height
        center-height                      (* 7 top-height)
        icon-height                        (/ card-height 14.05)
        border                             "3px solid black"
        line-height                        (/ card-height 11)
        font-size                          line-height
        border-radius                      (/ card-height 20) #_ (/ card-height 11.8)
        symbol-padding-from-horizontal-rim (/ card-height 14.75)
        symbol-width                       (/ card-width-in-vw 6)
        symbol-height                      symbol-width]
    [:div.card {:style {:width           (vw card-width)
                        :min-height      "100%" ;; This fixes problem where differences in floating point calculation results would sometime cause the bottom to become disconnected from the top, as well as (as a side-effect, I guess) sometimes causing a gap between some of the tops of layered cards, such as cards 9. and 10 being separated height-wise by a couple of pixels.
                        :display         :flex
                        :flex-direction  :column
                        :justify-content :space-between
                        :opacity                 (cond
                                                   selected? 1
                                                   @(re-frame.core/subscribe [:mouse-over? color-identity card-identity]) 0.3
                                                   :else 0)}}
     [:div.top {:on-mouse-over (fn [event]
                                  (re-frame.core/dispatch [:mousing-over color-identity card-identity]))
                :on-mouse-out (fn [event]
                                  (re-frame.core/dispatch [:mouse-leave color-identity card-identity]))
                :on-click     (fn [event]
                                (re-frame.core/dispatch [:mouse-toggle-card color-identity card-identity]))
                :style {:display                 :flex
                        :justify-content         :space-between
                        :align-items             :center
                        :height                  (vw top-height)
                        :box-sizing              "border-box"
                        :border-top-left-radius  (vw border-radius)
                        :border-top-right-radius (vw border-radius)
                        :padding-left            (vw symbol-padding-from-horizontal-rim)
                        :padding-right           (vw symbol-padding-from-horizontal-rim)
                        :line-height             (vw line-height)
                        :font-size               (vw font-size)
                        :color                   color
                        :background-color        "rgba(0,0,0,1)"}}
      [card-symbol {:card-identity card-identity
                    :class         "card-symbol-left"
                    :height        symbol-height
                    :width         symbol-width
                    :fill          color}]
      [card-symbol {:card-identity card-identity
                    :class         "card-symbol-right"
                    :height        symbol-height
                    :width         symbol-width
                    :fill          color}]]
     [:div.center {:style {:height           (vw center-height)
                           :background-color color
                           :border-left      border
                           :border-right     border}}]
     [:div.bottom {:style {:height                     (vw top-height)
                           :box-sizing                 "border-box"
                           :border-left                border
                           :border-right               border
                           :border-bottom              border
                           :border-bottom-left-radius  (vw border-radius)
                           :border-bottom-right-radius (vw border-radius)
                           :padding-left               20
                           :background-color           color}}]]))

(defn stack [{:keys [card-values
                     card-width-in-vw
                     color-identity]}]
  [:div {:style {:width (vw card-width-in-vw)}}
   (->> card-values
        (map-indexed
         (fn [index [card-identity selected?]]
           ^{:key card-identity}
           [:div
            {:style {:margin-top (if (= 0 index)
                                   0
                                   (let [card-height          (->> card-width-in-vw
                                                                   card-width->card-height)
                                         negative-card-height (- card-height)
                                         card-top             (->> card-width-in-vw
                                                                   card-width->card-height
                                                                   height-of-card-top)
                                         distance             (+ negative-card-height card-top)]
                                     (vw distance)))
                     :z-index    index       ;; Need z-index in order to prevent flickering of all but the top card when I mouseover their top.
                     :position   :relative}} ;;Need a :position value in order for z-index to come into effect.
            [resizable-card {:color-identity   color-identity
                             :selected?        selected?
                             :card-identity    card-identity
                             :card-width-in-vw card-width-in-vw}]])))])

(defn stack-container [{:keys [:index :card-width-in-vw :color-identity :card-values]}]
  [:div
   [:div {:style {:height (vw card-width-in-vw)
                  :width  (vw card-width-in-vw)}}
    [:svg {:width  "100%"
           :height "100%"}
     [:circle {:cx           "50%"
               :cy           "50%"
               :r            "15%"
               :stroke       :black
               :stroke-width (if @(re-frame.core/subscribe [:show-indicator? color-identity])
                               "2%"
                               "0.1%")
               :fill         (get color-identities->rgb-colors color-identity)}]]]
   [stack {:card-values      card-values
           :card-width-in-vw card-width-in-vw
           :color-identity   color-identity}]
   [:div {:style {:text-align :center}}
    @(re-frame.core/subscribe [:score-for-color color-identity])]])

(defn stacks [{:keys [:card-width-in-vw :card-values] :as args}]
  [:div {:style {:display         :flex
                 :justify-content :space-between
                 :width           "70%"}}
   (->> color-identities
        (map-indexed (fn [index color-identity]
                       ^{:key color-identity}
                       [stack-container {:card-values      (get card-values color-identity)
                                         :card-width-in-vw card-width-in-vw
                                         :index            index
                                         :color-identity   color-identity}])))])

(defn home-page []
  [:div {:style {:display        :flex
                 :min-height     "100vh"
                 :align-items    :center
                 :justify-content :space-between
                 :flex-direction :column}}
   [:div {:style {:display         :flex
                  :flex-direction  :column
                  :width           "80%"
                  :align-items     :center}}
    [:div {:style {:height          "10vwh"
                   :width           "100%"
                   :display         :flex
                   :justify-content :flex-end}}
     #_[:button {:on-click #(re-frame.core/dispatch [:toggle-score-details])}
      "Toggle details"]]
    [stacks {:card-width-in-vw 10
             :card-values      @(re-frame.core/subscribe [:cards])}]]
   [:div {:style {:margin-bottom "2%"}}
    (str "Total score: " @(re-frame.core/subscribe [:score]))]])
;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent.core/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (re-frame.core/dispatch-sync [:initialize-db])
  (add-event-dispatcher-for-keydown)
  (mount-root))

