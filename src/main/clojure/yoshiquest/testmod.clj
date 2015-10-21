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

;Creates a tile entity called "tile-block-entity" with a field named "something" with an initial value of "0".
(deftileentity yoshiquest.testmod.tileentities tile-block-entity :fields {:something 0})

;Creates a new instance of tile-block-entity.
(defn new-tile-block-entity [world metadata]
  (.newInstance ^Class tile-block-entity))

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
  :creative-tab CreativeTabs/tabBlock
  :block-texture-name "test-mod:tile-block")

;Called on the server upon receiving a packet from the client, printing out the received value.
(defn on-server-packet [nbt-map context]
  (println (str "Server: " nbt-map)))

;Future declaration for the network created in the pre-init function.
(declare test-mod-server-network)

;Creates a packet handler named "test-mod-server-network-handler" (what a mouthfull), providing on-server-packet
;as the event called upon receiving a packet.
(gen-packet-handler yoshiquest.testmod test-mod-server-network-handler on-server-packet)

;Right click function for the net-test item. Simply sends a message to the server.
(defn right-click-send [istack world player]
  (if (remote? world)
    (send-to-server test-mod-server-network {:message "Hello World"}))
  istack)

;Test item to test the network system.
(defitem net-test
  :unlocalized-name "net-test"
  :creative-tab CreativeTabs/tabMisc
  :override {:on-item-right-click right-click-send})

;Creates an event handler with the PlayerPickupXpEvent, and the EntityConstructing event.
;The full package names are required.
(gen-events yoshiquest.testmod common-event-handler
            :xpEvent {:event net.minecraftforge.event.entity.player.PlayerPickupXpEvent}
            :entityConstructingEvent {:event net.minecraftforge.event.entity.EntityEvent$EntityConstructing})

;Fires on the PlayerPickupXpEvent, and therefore prints out "Event!" when the player picks up experience.
(defn common-event-handler-xpEvent [this event]
  (println "Event!"))

;Creates extended properties called test-properties with the field "tacopower"
;(it was the first thing that popped into my head ok), which is initially set to "0".
(defextendedproperties yoshiquest.testmod test-properties :fields {:tacopower 0})

;Fires on the EntityConstructing event, initializing the new properties for each player.
(defn common-event-handler-entityConstructingEvent [this ^net.minecraftforge.event.entity.EntityEvent$EntityConstructing event]
  (let [entity (.-entity event)]
    (when (and (instance? net.minecraft.entity.player.EntityPlayer entity) (nil? (.getExtendedProperties entity "test-properties")))
      (register-extended-properties entity "test-properties" (.newInstance ^Class test-properties)))))

;Right click function for testing the extended entity properties. Prints out the current "tacopower",
;and increases it by 1.
(defn right-click-property [istack world player]
  (when (not (remote? world))
    (let [test-properties (get-extended-properties player "test-properties")]
      (println (str "Tacopower: " (:tacopower test-properties)))
      (assoc! test-properties :tacopower (inc (:tacopower test-properties)))))
  istack)

;Creates an item to test extended entity properties.
(defitem property-test
  :unlocalized-name "property-test"
  :creative-tab CreativeTabs/tabMisc
  :override {:on-item-right-click right-click-property})

;Pre-init function, registers a tile entity, defines a new network with id "test-net", and registers a new message for the network.
(defn common-pre-init [this event]
  (register-tile-entity tile-block-entity "test-mod-tile-block-entity")
  (def test-mod-server-network (create-network "test-net"))
  (register-message test-mod-server-network test-mod-server-network-handler 0 :server))

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
  (register tile-block "tile-block")
  (register net-test "net-test")
  (register property-test "property-test")
  (register testgen)
  (register-events common-event-handler)
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
(defmod yoshiquest.testmod test-mod "0.3.0"
  :common {:pre-init common-pre-init
           :init common-init})
