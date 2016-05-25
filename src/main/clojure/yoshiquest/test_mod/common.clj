(ns yoshiquest.test-mod.common
  (:require
    [forge-clj.registry :refer [register]]
    [yoshiquest.test-mod.blocks :refer [test-block]]))

(defn common-init [this event]
  (register test-block "test-block")
  )