(ns yoshiquest.test-mod.tileentities
  (:require
   [forge-clj.tileentity :refer [deftileentity get-tile-entity-at]]
   [forge-clj.util :refer [get-block itemstack]])
  (:import
   [net.minecraft.inventory IInventory]
   [net.minecraft.entity.player EntityPlayer]
   [net.minecraft.item ItemStack]
   [net.minecraft.tileentity TileEntity]))

;Creates a tile entity called "tile-block-entity" with a field named "something" with an initial value of "0".
(deftileentity yoshiquest.test-mod.tileentities tile-block-entity
  :fields {:something (itemstack (get-block "test-mod:test-block"))})

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

(deftileentity yoshiquest.test-mod.tileentities test-model-entity)

(defn new-test-model-entity [& args]
  (.newInstance ^Class test-model-entity))

(deftileentity yoshiquest.test-mod.tileentities test-inventory-entity
  :interfaces [net.minecraft.inventory.IInventory]
  :fields {:inv (into [] (repeat 9 nil))})

(def test-inventory-entity-getSizeInventory (constantly 9))

(defn test-inventory-entity-getStackInSlot [this slot]
  (get (:inv this) slot))

(def test-inventory-entity-getInventoryStackLimit (constantly 64))

(defn test-inventory-entity-setInventorySlotContents [this slot ^ItemStack stack]
  (if (and stack (> (.-stackSize stack) (test-inventory-entity-getInventoryStackLimit this)))
    (set! (.-stackSize stack) (test-inventory-entity-getInventoryStackLimit this)))
  (assoc! this :inv (assoc (:inv this) slot stack)))

(defn test-inventory-entity-decrStackSize [this slot amount]
  (let [^ItemStack stack (test-inventory-entity-getStackInSlot this slot)]
    (when stack
      (if (<= (.-stackSize stack) amount)
        (test-inventory-entity-setInventorySlotContents this slot nil)
        (do
          (.splitStack stack amount)
          (if (= (.-stackSize stack) 0)
            (test-inventory-entity-setInventorySlotContents this slot nil)))))
    stack))

(defn test-inventory-entity-getStackInSlotOnClosing [this slot]
  (let [stack (test-inventory-entity-getStackInSlot this slot)]
    (if stack
      (test-inventory-entity-setInventorySlotContents this slot nil))
    stack))

(defn test-inventory-entity-isUsableByPlayer [^TileEntity this ^EntityPlayer player]
  (and (= (get-tile-entity-at (.-xCoord this) (.-yCoord this) (.-zCoord this)) this) (< (.getDistanceSq player (+ 0.5 (.-xCoord this)) (+ 0.5 (.-yCoord this)) (+ 0.5 (.-zCoord this))) 64)))

(def test-inventory-entity-openInventory (constantly nil))
(def test-inventory-entity-closeInventory (constantly nil))
(def test-inventory-entity-hasCustomInventoryName (constantly true))
(def test-inventory-entity-getInventoryName (constantly "test-mod.test-inventory-entity"))
(def test-inventory-entity-isItemValidForSlot (constantly true))

(defn new-test-inventory-entity [& args]
  (.newInstance ^Class test-inventory-entity))
