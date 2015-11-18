(ns yoshiquest.test-mod.client.ui
  (:require
   [forge-clj.client.ui :refer [make-gui-container draw-rect]]
   [forge-clj.tileentity :refer [get-tile-entity-at]]
   [yoshiquest.test-mod.ui :refer [get-client-gui get-server-gui]]))

(defn client-gui-fn [id player world x y z]
  (condp = id
    0 (make-gui-container (get-server-gui 0 player world x y z) (constantly nil) (constantly nil))
    nil))

(defn init-client-gui []
  (reset! get-client-gui client-gui-fn))
