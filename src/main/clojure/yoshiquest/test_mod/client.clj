(ns yoshiquest.test-mod.client
  (:require
    [forge-clj.client.registry :refer [register-model add-variants]]
    [yoshiquest.test-mod.blocks :refer [test-block meta-block-item]]))

(def meta-block-variants ["test-mod:meta-block-black" "test-mod:meta-block-red" "test-mod:meta-block-green" "test-mod:meta-block-blue" "test-mod:meta-block-yellow" "test-mod:meta-block-purple"])

(defn client-pre-init [this event]
  (add-variants meta-block-item meta-block-variants))

(defn client-init [this event]
  (register-model test-block 0 "test-mod:test-block")
  (dorun (map (partial register-model meta-block-item) (range (count meta-block-variants)) meta-block-variants)))
