(ns yoshiquest.test-mod.tileentities
  (:require
   [forge-clj.tileentity :refer [deftileentity]]))

;Creates a tile entity called "tile-block-entity" with a field named "something" with an initial value of "0".
(deftileentity yoshiquest.test-mod.tileentities tile-block-entity :fields {:something 0})

;Creates a new instance of tile-block-entity.
(defn new-tile-block-entity [world metadata]
  (.newInstance ^Class tile-block-entity))

(deftileentity yoshiquest.test-mod.tileentities render-block-entity
  :fields {:rotation 0
           :yaw 0
           :pitch 0}
  :sync-data [:rotation :yaw :pitch])

(defn render-block-entity-updateEntity [this]
  (assoc! this :rotation (if (>= (+ 0.1 (:rotation this)) 3.1)
                           0
                           (+ (:rotation this) 0.1))))

(defn new-render-block-entity [world & args]
  (.newInstance ^Class render-block-entity))
