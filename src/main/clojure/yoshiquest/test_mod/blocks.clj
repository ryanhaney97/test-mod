(ns yoshiquest.test-mod.blocks
  (:require
    [forge-clj.blocks :refer [defblock defitemblock get-state]]
    [forge-clj.util :refer [itemstack remote? get-tile-entity-at printchat drop-items open-gui]]
    [yoshiquest.test-mod.tileentities :refer [new-tile-block-entity new-render-block-entity new-test-model-entity new-test-inventory-entity]])
  (:import
    [net.minecraft.block Block]
    [net.minecraft.creativetab CreativeTabs]
    [net.minecraft.item ItemBlock ItemStack]
    [net.minecraft.util BlockPos]))

(defblock test-block
          :hardness 0.5
          :creative-tab CreativeTabs/tabBlock
          :light-level (float 1.0)
          :step-sound Block/soundTypeStone)

(defblock meta-block
          :hardness 0.5
          :creative-tab CreativeTabs/tabBlock
          :step-sound Block/soundTypeCloth
          :state {:color [:black :red :green :blue :yellow :purple]}
          :override {:get-sub-blocks (fn [item _ ^java.util.List list]
                                       (dorun (map #(.add list (itemstack item 1 %1)) (range 6))))
                     :damage-dropped (fn [state]
                                       (.getMetaFromState ^Block this state))})

(defitemblock meta-block-item meta-block
              :max-damage 0
              :has-subtypes true
              :override {:get-metadata (fn [istack]
                                         (if (instance? ItemStack istack)
                                           (.getItemDamage ^ItemStack istack)
                                           istack))
                         :get-unlocalized-name (fn [items]
                                                 (let [this ^ItemBlock this]
                                                   (str (proxy-super getUnlocalizedName items) "-" (name (:color (get-state meta-block items))))))})

(defblock facing-meta-block
          :hardness 0.5
          :creative-tab CreativeTabs/tabBlock
          :step-sound Block/soundTypeCloth
          :state {:color [:red :green]
                  :facing [:up :down]}
          :override {:get-sub-blocks (fn [item _ ^java.util.List list]
                                       (dorun (map #(.add list (itemstack item 1 %1)) (range 4))))
                     :damage-dropped (fn [state]
                                       (.getMetaFromState ^Block this state))})

(defitemblock facing-meta-block-item facing-meta-block
              :max-damage 0
              :has-subtypes true
              :override {:get-metadata (fn [istack]
                                         (if (instance? ItemStack istack)
                                           (.getItemDamage ^ItemStack istack)
                                           istack))
                         :get-unlocalized-name (fn [items]
                                                 (let [this ^ItemBlock this
                                                       state (get-state facing-meta-block items)]
                                                   (str (proxy-super getUnlocalizedName items) "-" (name (:color state)) "-" (name (:facing state)))))})

(defn on-tile-block-click [world block-pos state player side hit-x hit-y hit-z]
  (when (not (remote? world))
    (let [tile-entity (get-tile-entity-at world (.getX ^BlockPos block-pos) (.getY ^BlockPos block-pos) (.getZ ^BlockPos block-pos))]
      (printchat player (str "Something: " (:something tile-entity)))
      (assoc! tile-entity :something (inc (:something tile-entity)))))
  false)

(defblock tile-block
          :container? true
          :override {:create-new-tile-entity new-tile-block-entity
                     :on-block-activated on-tile-block-click}
          :hardness 0.5
          :step-sound Block/soundTypeStone
          :creative-tab CreativeTabs/tabBlock)

(defn store-rotation [world ^BlockPos block-pos block-state ^net.minecraft.entity.EntityLivingBase player istack]
  (assoc! (get-tile-entity-at world (.getX block-pos) (.getY block-pos) (.getZ block-pos)) :pitch (.-rotationPitch player) :yaw (.-rotationYaw player)))

(defblock render-block
          :container? true
          :override {:create-new-tile-entity new-render-block-entity
                     :is-opaque-cube (constantly false)
                     :render-as-normal-block (constantly false)
                     :on-block-placed-by store-rotation}
          :hardness 0.5
          :light-opacity 0
          :step-sound Block/soundTypeStone
          :creative-tab CreativeTabs/tabBlock)

(defblock test-model
          :container? true
          :override {:create-new-tile-entity new-test-model-entity
                     :is-opaque-cube (constantly false)
                     :render-as-normal-block (constantly false)}
          :hardness 0.5
          :light-opacity 0
          :step-sound Block/soundTypeStone
          :creative-tab CreativeTabs/tabBlock)

(def mod-instance (atom nil))

(defn open-test-inventory-gui [world ^BlockPos pos state player side hit-x hit-y hit-z]
  (if (not (remote? world))
    (open-gui player (deref mod-instance) 0 world (.getX pos) (.getY pos) (.getZ pos)))
  true)

(defblock test-inventory
          :container? true
          :override {:create-new-tile-entity new-test-inventory-entity
                     :break-block (fn [world ^BlockPos pos state]
                                    (drop-items world (.getX pos) (.getY pos) (.getZ pos))
                                    (let [this ^Block this]
                                      (proxy-super breakBlock world pos state)))
                     :on-block-activated open-test-inventory-gui}
          :hardness 0.5
          :step-sound Block/soundTypeStone
          :creative-tab CreativeTabs/tabBlock)