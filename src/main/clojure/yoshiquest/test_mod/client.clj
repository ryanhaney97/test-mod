(ns yoshiquest.test-mod.client
  (:require
    [forge-clj.client.registry :refer [register-model]]
    [yoshiquest.test-mod.blocks :refer [test-block]]))

(defn client-init [this event]
  (register-model test-block 0 "test-mod:test-block"))
