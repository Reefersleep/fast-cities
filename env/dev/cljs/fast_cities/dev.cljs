(ns ^:figwheel-no-load fast-cities.dev
  (:require
    [fast-cities.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
