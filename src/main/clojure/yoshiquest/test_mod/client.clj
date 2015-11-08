(ns yoshiquest.test-mod.client
  (:require
   [forge-clj.renderer :refer [bind-tile-renderer]]
   [yoshiquest.test-mod.tileentities :refer [render-block-entity]]
   [yoshiquest.test-mod.renderers.render-block-renderer :refer [render-block-tile-renderer]]))

(defn client-init [this event]
  (bind-tile-renderer render-block-entity render-block-tile-renderer))
