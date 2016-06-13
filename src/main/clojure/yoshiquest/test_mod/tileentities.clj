(ns yoshiquest.test-mod.tileentities
  (:require
    [forge-clj.tileentity :refer [deftileentity]]
    [forge-clj.util :refer [get-block itemstack with-prefix get-tile-entity-at]])
  (:import
    [net.minecraft.item ItemStack]
    [net.minecraft.tileentity TileEntity]
    [net.minecraft.entity.player EntityPlayer]))

;Creates a tile entity called "tile-block-entity" with a field named "something" with an initial value of "0".
(deftileentity tile-block-entity
               :fields {:something 0})

;Creates a new instance of tile-block-entity.
(defn new-tile-block-entity [world blockstate]
  (.newInstance ^Class tile-block-entity))

(deftileentity render-block-entity
               :fields {:rotation 0
                        :yaw 0
                        :pitch 0}
               :sync-data [:rotation :yaw :pitch]
               :interfaces [net.minecraft.util.ITickable])

(with-prefix render-block-entity-
             (defn update [this]
               (assoc! this :rotation (if (>= (+ 0.1 (:rotation this)) 3.1)
                                        0
                                        (+ (:rotation this) 0.1)))))

(defn new-render-block-entity [world & args]
  (.newInstance ^Class render-block-entity))

(deftileentity test-model-entity)

(defn new-test-model-entity [& args]
  (.newInstance ^Class test-model-entity))

(deftileentity test-inventory-entity
               :interfaces [net.minecraft.inventory.IInventory]
               :fields {:inv (into [] (repeat 9 nil))})

(with-prefix test-inventory-entity-
             (def getSizeInventory (constantly 9))

             (defn getStackInSlot [this slot]
               (get (:inv this) slot))

             (def getInventoryStackLimit (constantly 64))

             (defn setInventorySlotContents [this slot ^ItemStack stack]
               (if (and stack (> (.-stackSize stack) (test-inventory-entity-getInventoryStackLimit this)))
                 (set! (.-stackSize stack) (test-inventory-entity-getInventoryStackLimit this)))
               (assoc! this :inv (assoc (:inv this) slot stack)))

             (defn decrStackSize [this slot amount]
               (let [^ItemStack stack (test-inventory-entity-getStackInSlot this slot)]
                 (when stack
                   (if (<= (.-stackSize stack) amount)
                     (test-inventory-entity-setInventorySlotContents this slot nil)
                     (do
                       (.splitStack stack amount)
                       (if (= (.-stackSize stack) 0)
                         (test-inventory-entity-setInventorySlotContents this slot nil)))))
                 stack))

             (defn getStackInSlotOnClosing [this slot]
               (let [stack (test-inventory-entity-getStackInSlot this slot)]
                 (if stack
                   (test-inventory-entity-setInventorySlotContents this slot nil))
                 stack))
             (defn isUsableByPlayer [^TileEntity this ^EntityPlayer player]
               (let [block-pos (.getPos this)]
                 (and (= (get-tile-entity-at (.getX block-pos) (.getY block-pos) (.getZ block-pos)) this) (< (.getDistanceSq player (+ 0.5 (.getX block-pos)) (+ 0.5 (.getY block-pos)) (+ 0.5 (.getZ block-pos))) 64))))

             (def openInventory (constantly nil))
             (def closeInventory (constantly nil))
             (def hasCustomInventoryName (constantly true))
             (def getInventoryName (constantly "test-mod.test-inventory-entity"))
             (def isItemValidForSlot (constantly true)))

(defn new-test-inventory-entity [& args]
  (.newInstance ^Class test-inventory-entity))