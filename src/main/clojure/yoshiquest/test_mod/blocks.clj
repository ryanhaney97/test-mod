(ns yoshiquest.test-mod.blocks
  (:require
   [forge-clj.blocks :refer [defblock defblockitem]]
   [forge-clj.tileentity :refer [get-tile-entity-at]]
   [forge-clj.util :refer [remote? itemstack deftab]]
   [yoshiquest.test-mod.items :refer [test-item]]
   [yoshiquest.test-mod.tab :refer [tab-test-mod]]
   [yoshiquest.test-mod.tileentities :refer [new-tile-block-entity new-render-block-entity]])
  (:import
   [net.minecraft.block Block]))

;Creates a simple test block with a light level of 1.0, and adds it to the "block" tab in creative mode.
(defblock test-block
  :block-name "test-block"
  :override {:get-item-dropped (constantly test-item)}
  :hardness 0.5
  :step-sound Block/soundTypeStone
  :creative-tab tab-test-mod
  :light-level (float 1.0)
  :block-texture-name "test-mod:test-block")

;The following creates another test block, this time with a different texture on each side.
;This stores the icons in the "icons" atom, registers them with the register-multiblock-icons function,
;and then obtains the respective icon using the get-multiblock-icon function.

;This stores the icons for the block (as well as the metablock further on).
(def icons (atom {}))

;Realize that I used the ^ symbol here to supply a type for reg.
;If you have a forge object being passed in as an argument,
;and need to call a non-static method on that argument,
;then be sure to do this. Otherwise, clojure will resolve the call at run-time,
;rather than compile-time, resulting in the name not getting reobfuscated.
(defn register-multiblock-icons [^net.minecraft.client.renderer.texture.IIconRegister reg]
  (let [names (map (partial str "test-mod:multiblock_") (range 6))
        register-icon (fn [iname]
                        (.registerIcon reg iname))
        temp-icons (doall (map register-icon names))
        icon-map (zipmap (range 6) temp-icons)]
    (reset! icons icon-map)))

;Note that for get-multiblock-icon, we have to override both arities of the function, despite only using one,
;since in the case of overriden methods, the function is called for all types and arities.
(defn get-multiblock-icon
  ([side metadata]
   (get @icons side))
  ([_ _ _ _ side]
   (get-multiblock-icon side nil)))

;Finally, this declares the block itself.
(defblock multiblock
  :block-name "multiblock"
  :override {:register-block-icons register-multiblock-icons
             :get-icon get-multiblock-icon}
  :hardness 0.5
  :step-sound Block/soundTypeStone
  :creative-tab tab-test-mod
  :block-texture-name "test-mod:multiblock")

;The following is a test to add a block with metadata to minecraft called metablock.

;This is similar to the previous multiblock implementation, except in this case we DO care about the metadata,
;so we simply do what the normal version does.
(defn get-metablock-icon
  ([side metadata]
   (let [data (get @icons metadata)]
     (if data
       data
       (get @icons 0))))
  ([^net.minecraft.world.IBlockAccess blockaccess arg1 arg2 arg3 side]
   (get-metablock-icon side (.getBlockMetadata blockaccess arg1 arg2 arg3))))

;This adds a list of sub blocks to the provided list.
(defn metablock-sub-blocks [item tab ^java.util.List stacklist]
  (let [meta-vals (range 6)
        istacks (map (partial itemstack item 1) meta-vals)
        addtolist (fn [istack]
                    (.add stacklist istack))]
    (doall (map addtolist istacks))))

;Finally, this creates the actual block. Constantly is used since we don't want register-block-icons to do anything,
;so we just make it always return nil. Damage dropped needs to return the provided argument,
;so the identity function is used in this case.
(defblock metablock
  :block-name "metablock"
  :override {:register-block-icons (constantly nil)
             :get-icon get-metablock-icon
             :damage-dropped identity
             :get-sub-blocks metablock-sub-blocks}
  :hardness 0.5
  :step-sound Block/soundTypeStone
  :creative-tab tab-test-mod)

;This creates the item version of the block. The metadata? tag will make it an ItemBlockWithMetadata rather than an
;ItemBlock. Since the name of the core block isn't changing, I simply used the name, an underscore,
;and the block damage for the name.
(defblockitem metablockitem metablock
  :metadata? true
  :override {:get-unlocalized-name (fn
                                     ([^net.minecraft.item.ItemStack istack]
                                      (str "metablock_" (.getItemDamage istack)))
                                     ([]
                                      "metablock"))})

;Called on right click. Gets the tile entity at the position, prints the value of the "something" field, and
;increases the "something" field by one.
(defn on-tile-block-click [world x y z player _ _ _ _]
  (when (not (remote? world))
    (let [tile-entity (get-tile-entity-at world x y z)]
      (println (str "Something: " (:something tile-entity)))
      (assoc! tile-entity :something (inc (:something tile-entity)))))
  false)

;Makes a block container with the tile-block-entity as the tile entity it uses.
(defblock tile-block
  :block-name "tile-block"
  :container? true
  :override {:create-new-tile-entity new-tile-block-entity
             :on-block-activated on-tile-block-click}
  :hardness 0.5
  :step-sound Block/soundTypeStone
  :creative-tab tab-test-mod)

(defn store-rotation [world x y z ^net.minecraft.entity.EntityLivingBase player _]
  (assoc! (get-tile-entity-at world x y z) :pitch (.-rotationPitch player) :yaw (.-rotationYaw player)))

(defblock render-block
  :block-name "render-block"
  :container? true
  :override {:create-new-tile-entity new-render-block-entity
             :is-opaque-cube (constantly false)
             :render-as-normal-block (constantly false)
             :get-render-type (constantly -1)
             :on-block-placed-by store-rotation}
  :hardness 0.5
  :light-opacity 0
  :step-sound Block/soundTypeStone
  :creative-tab tab-test-mod)
