(ns yoshiquest.test-mod.ui
  (:require
   [forge-clj.ui :refer [defcontainer defguihandler]]
   [forge-clj.util :refer [construct with-prefix get-tile-entity-at]])
  (:import
   [net.minecraft.entity.player EntityPlayer]
   [net.minecraft.inventory Slot]
   [net.minecraft.item ItemStack]))

(def get-client-gui (atom (constantly nil)))

(def slots (into []
                 (for [x (range 3)
                       y (range 3)]
                   [(+ x (* y 3)) (+ (* x 18) 62) (+ (* y 18) 17)])))

(defcontainer test-container
  :player-hotbar? true
  :player-inventory? true
  :slots slots)

(with-prefix test-container-
  (defn transferStackInSlot [^TestContainer this ^EntityPlayer player slot-index]
    (let [^Slot slot (.get ^java.util.List (.-inventorySlots this) slot-index)]
      (if (and slot (.getHasStack slot))
        (let [^ItemStack istack (.getStack slot)
              ^ItemStack prev (.copy istack)]
          (when (< slot-index 9)
            (.mergeItemStack this istack 9 45 true))
          (when (>= slot-index 9)
            (.mergeItemStack this istack 0 9 false))
          (if (= (.-stackSize istack) 0)
            (.putStack slot nil)
            (.onSlotChanged slot))
          (when (not= (.-stackSize istack) (.-stackSize prev))
            (.onPickupFromSlot slot player istack)
            prev)))))

  (defn canInteractWith [^TestContainer this player]
    true))

(defn get-server-gui [id ^EntityPlayer player world x y z]
  (if (get-tile-entity-at world x y z)
    (condp = id
      0 (construct test-container (.-inventory player) (get-tile-entity-at world x y z))
      nil)))

(defguihandler test-mod-gui-handler
  get-server-gui
  (deref get-client-gui))
