(ns yoshiquest.test-mod.client.renderers.test-model-renderer
  (:require
   [forge-clj.client.renderer :refer [deftilerenderer]]))

(def test-model
  {:shape1
   {:texture-offset {:x 0, :y 0},
    :rotation-point {:x 8.0, :y 8.0, :z 8.0},
    :box {:x -4.0, :y -4.0, :z -4.0, :width 8, :height 8, :depth 8},
    :rotation {:x 0.7740535, :y -0.74700093, :z 0.27314404}},
   :shape2
   {:texture-offset {:x 0, :y 0},
    :rotation-point {:x 8.0, :y 8.0, :z 8.0},
    :box {:x -8.0, :y -8.0, :z -8.0, :width 16, :height 16, :depth 16},
    :rotation {:x 0.0, :y 0.0, :z 0.0},
    :opacity 0.3}})

(deftilerenderer test-model-tile-renderer (constantly test-model)
  :texture "test-mod:textures/blocks/multiblock_2.png"
  :memo? true)
