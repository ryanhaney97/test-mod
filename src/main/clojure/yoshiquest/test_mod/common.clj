(ns yoshiquest.test-mod.common
  (:require
    [forge-clj.registry :refer [register register-block register-item register-events]]
    [forge-clj.util :refer [get-block]]
    [yoshiquest.test-mod.blocks :refer [test-block meta-block meta-block-item facing-meta-block facing-meta-block-item]]
    [yoshiquest.test-mod.items :refer [test-item test-shovel test-boots test-food net-test]]
    [yoshiquest.test-mod.events :refer [common-event-handler]]
    [yoshiquest.test-mod.network :refer [init-network]]))

(defn common-pre-init [_ _]
  (init-network))

(defn common-init [_ _]
  (register test-item "test-item")
  (register-item test-shovel "test-shovel")
  (register-item test-boots "test-boots")
  (register-item test-food "test-food")
  (register-item net-test "net-test")
  (register test-block "test-block")
  (register-block meta-block "meta-block" meta-block-item)
  (register-block facing-meta-block "facing-meta-block" facing-meta-block-item)
  (register-events common-event-handler))