(ns yoshiquest.test-mod.renderers.render-block-renderer
  (:require
   [forge-clj.renderer :refer [deftilerenderer]]
   [forge-clj.util :refer [defmemo abs]]))

(defn within [value small big]
  (and (>= value small) (<= value big)))

(defmemo get-axis [yaw pitch]
  (let [yaw-within (partial within (abs yaw))]
    (if (and (> pitch -30) (< pitch 30))
      (if (or (yaw-within 315 360) (yaw-within 0 45) (yaw-within 135 225))
        :z
        :x)
      :y)))

(defmemo get-render-block-model [axis]
  (let [base-model {:simple-render {:box {:x -1.5 :y -1.5 :z -1.5 :width 3 :height 3 :depth 3}
                                    :rotation-point {:x 8 :y 8 :z 8}
                                    :rotation {:x 0 :y 0 :z 0}}}
        side-type {:x :width :y :height :z :depth}]
    (-> base-model
        (assoc-in [:simple-render :box axis] 0)
        (assoc-in [:simple-render :box (get side-type axis)] 16)
        (assoc-in [:simple-render :rotation-point axis] 0))))

(defmemo rotate-model [model axis rotation]
  (assoc-in model [:simple-render :rotation axis] (+ (get-in model [:simple-render :rotation axis]) rotation)))

(defn update-render-block-model [entity]
  (let [axis (get-axis (:yaw entity) (:pitch entity))
        model (get-render-block-model axis)]
    (rotate-model model axis (:rotation entity))))

(deftilerenderer render-block-tile-renderer update-render-block-model
  :texture "test-mod:textures/blocks/multiblock_2.png"
  :memo? true)
