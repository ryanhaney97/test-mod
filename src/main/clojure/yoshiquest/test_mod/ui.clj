(ns yoshiquest.test-mod.ui
  (:require
   [forge-clj.ui :refer [defguihandler make-container merge-item-stack]]
   [forge-clj.tileentity :refer [get-tile-entity-at]])
  (:import
   [net.minecraft.entity.player EntityPlayer]
   [net.minecraft.inventory Container Slot]
   [net.minecraft.item ItemStack]))

(def get-client-gui (atom (constantly nil)))

(def slots (into []
                 (for [x (range 3)
                       y (range 3)]
                   [(+ x (* y 3)) (+ (* x 18) 62) (+ (* y 18) 17)])))

(defn transfer-stack-in-slot [^Container this ^EntityPlayer player slot-index]
  (let [^Slot slot (.get ^java.util.List (.-inventorySlots this) slot-index)]
    (if (and slot (.getHasStack slot))
      (let [^ItemStack istack (.getStack slot)
            ^ItemStack prev (.copy istack)]
        (if (not (merge-item-stack this istack 9 45 true))
          nil
          (if (not (merge-item-stack this istack 0 9 false))
            nil
            (do
              (if (= (.-stackSize istack) 0)
                (.putStack slot nil)
                (.onSlotChanged slot))
              (if (= (.-stackSize istack) (.-stackSize prev))
                nil
                (do
                  (.onPickupFromSlot slot player istack)
                  prev)))))))))

(defn get-server-gui [id ^EntityPlayer player world x y z]
  (if (get-tile-entity-at world x y z)
    (condp = id
      0 (make-container (.-inventory player) (get-tile-entity-at world x y z)
                        :include-hotbar true
                        :include-inventory true
                        :slots slots
                        :override {:can-interact-with (constantly true)
                                   ;:transfer-stack-in-slot (fn [player slot-index]
                                   ;                          (transfer-stack-in-slot this player slot-index))
                                   })
      nil)))

(defguihandler test-mod-gui-handler
  get-server-gui
  (fn [id player world x y z]
    ((deref get-client-gui) id player world x y z)))
