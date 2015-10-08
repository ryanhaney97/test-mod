;Declares the namespace, and includes forge-clj as a required library.
(ns yoshiquest.testmod
  (:require
   [forge-clj.core :refer :all])
  (:import
   [net.minecraft.creativetab CreativeTabs]
   [net.minecraft.block Block]))

;Creates a simple test item, and adds it to the "misc" tab in creative mode.
(defitem testitem
  :unlocalized-name "testitem"
  :creative-tab CreativeTabs/tabMisc
  :texture-name "test-mod:testitem")

;Creates a simple test block with a light level of 1.0, and adds it to the "block" tab in creative mode.
(defblock testblock
  :block-name "testblock"
  :override {:get-item-dropped (constantly testitem)}
  :hardness 0.5
  :step-sound Block/soundTypeStone
  :creative-tab CreativeTabs/tabBlock
  :light-level (float 1.0)
  :block-texture-name "test-mod:testblock")

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
  :creative-tab CreativeTabs/tabBlock
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
(defn metablock-sub-blocks [item tab stacklist]
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
  :creative-tab CreativeTabs/tabBlock)

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

;Defines a tool material with the name testite and its respective properties.
(deftoolmaterial testite 3 1000 15.0 4.0 30)

;Creates a shovel using the testite material.
(deftool test-shovel testite :shovel
  :unlocalized-name "test-shovel")

;Creates an armor material for testite with these properties.
(defarmormaterial testite-armor 16 {:helmet 3
                                    :chestplate 8
                                    :leggings 6
                                    :boots 3} 30)

;Creates a pair of test boots.
(defarmor test-boots testite-armor :boots
  :unlocalized-name "test-boots")

;Creates a simple food item for testing.
(deffood test-nom 5 0.8
  :unlocalized-name "test-nom")

;A generator function for the testblock.
(defn gen-testblock [world random x z]
  (let [generator (net.minecraft.world.gen.feature.WorldGenMinable. testblock 8)]
    (run-default-generator generator world random x z 20 0 128)))

;The actual generator, passing in gen-testblock as the generation function.
(defgenerate testgen :overworld gen-testblock)

;Creates the initialization function for the mod itself, registering the previously defined blocks and items.
;Also adds a test recipe for testblock using testitem as an ingredient.
;Also registers the tool, armor, and generator previously defined.
(defn common-init [this event]
  (register testblock "testblock")
  (register testitem "testitem")
  (register multiblock "multiblock")
  (register metablock "metablock" metablockitem)
  (register test-shovel "test-shovel")
  (register test-boots "test-boots")
  (register test-nom "test-nom")
  (register testgen)
  (addrecipe testblock {:layout
                        "###
                        #_#
                        ###"
                        :bindings {\# testitem}})
  (addrecipe testitem {:shapeless true
                       :quantity 8
                       :items
                       [{:item testblock
                         :quantity 1}]})
  (addsmelting testblock testitem 1.0))

;Creates the mod itself, passing in the common-init function as the initializing function for the mod's common proxy.
(defmod yoshiquest.testmod test-mod "0.2.1"
  :common {:init common-init})
