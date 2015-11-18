(ns yoshiquest.test-mod.common
  (:require
   [forge-clj.core :refer [register register-tile-entity register-events]]
   [forge-clj.recipe :refer [addrecipe addsmelting]]
   [yoshiquest.test-mod.network :refer [init-network]]
   [yoshiquest.test-mod.blocks :refer [test-block multiblock metablock metablockitem tile-block render-block test-model test-inventory]]
   [yoshiquest.test-mod.items :refer [test-item test-shovel test-boots test-nom net-test property-test]]
   [yoshiquest.test-mod.events :refer [common-event-handler]]
   [yoshiquest.test-mod.tileentities :refer [tile-block-entity render-block-entity test-model-entity test-inventory-entity]]
   [yoshiquest.test-mod.world :refer [test-gen]]
   [clojure.java.io :as io]))

;Pre-init function, registers a tile entity, defines a new network with id "test-net", and registers a new message for the network.
(defn common-pre-init [this event]
  (register-tile-entity tile-block-entity "test-mod-tile-block-entity")
  (register-tile-entity render-block-entity "test-mod-render-block-entity")
  (register-tile-entity test-model-entity "test-mod-test-model-entity")
  (register-tile-entity test-inventory-entity "test-mod-test-inventory-entity")
  (init-network))

;Creates the initialization function for the mod itself, registering the previously defined blocks and items.
;Also adds a test recipe for testblock using testitem as an ingredient.
;Also registers the tool, armor, and generator previously defined.
(defn common-init [this event]
  (register test-block "test-block")
  (register test-item "test-item")
  (register multiblock "multiblock")
  (register metablock "metablock" metablockitem)
  (register test-shovel "test-shovel")
  (register test-boots "test-boots")
  (register test-nom "test-nom")
  (register tile-block "tile-block")
  (register net-test "net-test")
  (register property-test "property-test")
  (register render-block "render-block")
  (register test-model "test-model")
  (register test-inventory "test-inventory")
  (register test-gen)
  (register-events common-event-handler)
  (addrecipe test-block {:layout
                        "###
                        #_#
                        ###"
                        :bindings {\# test-item}})
  (addrecipe test-item {:shapeless true
                       :quantity 8
                       :items
                       [{:item test-block
                         :quantity 1}]})
  (addsmelting test-block test-item 1.0))
