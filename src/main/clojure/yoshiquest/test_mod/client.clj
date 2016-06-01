(ns yoshiquest.test-mod.client
  (:require
    [forge-clj.client.registry :refer [register-model add-variants]]
    [yoshiquest.test-mod.blocks :refer [test-block meta-block-item facing-meta-block-item]]
    [yoshiquest.test-mod.items :refer [test-item test-food]]))

(def meta-block-variants ["test-mod:meta-block-black" "test-mod:meta-block-red" "test-mod:meta-block-green" "test-mod:meta-block-blue" "test-mod:meta-block-yellow" "test-mod:meta-block-purple"])
(def facing-meta-block-variants ["test-mod:facing-meta-block-red-up" "test-mod:facing-meta-block-green-up" "test-mod:facing-meta-block-red-down" "test-mod:facing-meta-block-green-down"])

(defn client-pre-init [this event]
  (add-variants meta-block-item meta-block-variants)
  (add-variants facing-meta-block-item facing-meta-block-variants))

(defn client-init [this event]
  (register-model test-item "test-mod:test-item")
  (register-model test-block "test-mod:test-block")
  (register-model test-food "test-mod:test-food")
  (dorun (map (partial register-model meta-block-item) (range (count meta-block-variants)) meta-block-variants))
  (dorun (map (partial register-model facing-meta-block-item) (range (count facing-meta-block-variants)) facing-meta-block-variants)))
