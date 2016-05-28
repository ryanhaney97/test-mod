(ns yoshiquest.test-mod.common
  (:require
    [forge-clj.registry :refer [register register-block]]
    [forge-clj.util :refer [get-block]]
    [yoshiquest.test-mod.blocks :refer [test-block meta-block meta-block-item]]))

(defn common-init [this event]
  (register test-block "test-block")
  (register-block meta-block "meta-block" meta-block-item))