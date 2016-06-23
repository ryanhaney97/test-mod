(ns yoshiquest.test-mod.common
  (:require
    [forge-clj.registry :refer [register]]
    [yoshiquest.test-mod.blocks :refer [test-block meta-block meta-block-item facing-meta-block facing-meta-block-item tile-block render-block test-model mod-instance test-inventory]]
    [yoshiquest.test-mod.items :refer [test-item test-shovel test-boots test-food net-test property-test]]
    [yoshiquest.test-mod.events :refer [common-event-handler]]
    [yoshiquest.test-mod.world :refer [test-biome test-gen]]
    [yoshiquest.test-mod.tileentities :refer [tile-block-entity render-block-entity test-model-entity test-inventory-entity]]
    [yoshiquest.test-mod.ui :refer [test-mod-gui-handler]]))

(defn common-pre-init [this _]
  (reset! mod-instance this)
  (register tile-block-entity "test-mod-tile-block-entity")
  (register render-block-entity "test-mod-render-block-entity")
  (register test-model-entity "test-mod-test-model-entity")
  (register test-inventory-entity "test-mod-test-inventory-entity"))

(defn common-init [this _]
  (register test-item "test-item")
  (register test-shovel "test-shovel")
  (register test-boots "test-boots")
  (register test-food "test-food")
  (register net-test "net-test")
  (register property-test "property-test")
  (register test-block "test-block")
  (register meta-block "meta-block" meta-block-item)
  (register facing-meta-block "facing-meta-block" facing-meta-block-item)
  (register tile-block "tile-block")
  (register render-block "render-block")
  (register test-model "test-model")
  (register test-inventory "test-inventory")
  (register common-event-handler)
  (register this test-mod-gui-handler)
  (register test-gen)
  (register test-biome [:cool :warm] [:magical :sparse] 20))