(ns yoshiquest.test-mod.events
  (:require
    [forge-clj.registry :refer [register]]
    [forge-clj.event :refer [gen-events]]
    [forge-clj.util :refer [with-prefix printchat get-extended-properties remote?]]
    [yoshiquest.test-mod.entity :refer [test-properties mana-property]])
  (:import
    [net.minecraftforge.event.entity EntityEvent$EntityConstructing]
    [net.minecraftforge.event.entity.player EntityInteractEvent]))

;Creates an event handler with the PlayerPickupXpEvent, and the EntityConstructing event.
;The full package names are required if not using a forge or fml event.
(gen-events common-event-handler
            :xpEvent {:event :player-pickup-xp-event}
            :entityConstructingEvent {:event :entity-event.entity-constructing}
            :interactEvent {:event :entity-interact-event})

(with-prefix common-event-handler-
             (defn xpEvent [_ _]
               (println "Event!"))

             (defn entityConstructingEvent [this ^EntityEvent$EntityConstructing event]
               (let [entity (.-entity event)]
                 (if (and (instance? net.minecraft.entity.player.EntityPlayer entity) (nil? (get-extended-properties entity "test-properties")))
                   (do
                     (register entity "test-properties" (.newInstance ^Class test-properties))
                     (register entity "mana-property" (.newInstance ^Class mana-property)))
                   ;(if (and (instance? net.minecraft.entity.passive.EntitySheep entity) (nil? (get-extended-properties entity "mana-property")))
                   ;  (register entity "mana-property" (.newInstance ^Class mana-property)))
                   )))

             (defn interactEvent [this ^EntityInteractEvent event]
               (let [entity (.-target event)
                     player (.-entityPlayer event)
                     world (.-worldObj player)]
                 ;(if (instance? net.minecraft.entity.passive.EntitySheep entity)
                 ;  (let [properties (get-extended-properties entity "mana-property")]
                 ;    (if (remote? world)
                 ;      (assoc! properties :mana (inc (:mana properties)))
                 ;      (printchat player (str "Sheep Mana: " (:mana properties))))))
                 )))
