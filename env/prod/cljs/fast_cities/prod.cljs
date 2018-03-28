(ns fast-cities.prod
  (:require
    [fast-cities.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
