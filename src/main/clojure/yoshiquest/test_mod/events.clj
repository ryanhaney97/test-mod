(ns yoshiquest.test-mod.events
  (:require
    [forge-clj.registry :refer [register]]
    [forge-clj.event :refer [gen-events]]
    [forge-clj.util :refer [with-prefix printchat get-extended-properties remote? sync-data]]
    [clojure.core.async :refer [go timeout <!]]
    [yoshiquest.test-mod.entity :refer [test-properties mana-property]])
  (:import
    [net.minecraftforge.event.entity EntityEvent$EntityConstructing]
    [net.minecraftforge.event.entity.player EntityInteractEvent PlayerEvent$StartTracking]
    [net.minecraftforge.fml.common.gameevent PlayerEvent$PlayerLoggedInEvent]))

;Creates an event handler with the PlayerPickupXpEvent, and the EntityConstructing event.
;The full package names are required if not using a forge or fml event.
(gen-events common-event-handler
            :xpEvent {:event :player-pickup-xp-event}
            :entityConstructingEvent {:event :entity-event.entity-constructing}
            :interactEvent {:event :entity-interact-event}
            :trackingEvent {:event :player-event.start-tracking}
            :loginEvent {:event :player-event.player-logged-in-event})

(with-prefix common-event-handler-
             (defn xpEvent [_ _]
               (println "Event!"))

             (defn entityConstructingEvent [this ^EntityEvent$EntityConstructing event]
               (let [entity (.-entity event)]
                 (if (and (instance? net.minecraft.entity.player.EntityPlayer entity) (nil? (get-extended-properties entity "test-properties")))
                   (do
                     (register entity "test-properties" (.newInstance ^Class test-properties))
                     (register entity "mana-property" (.newInstance ^Class mana-property)))
                   (if (and (instance? net.minecraft.entity.passive.EntitySheep entity) (nil? (get-extended-properties entity "mana-property")))
                     (register entity "mana-property" (.newInstance ^Class mana-property))))))

             (defn interactEvent [this ^EntityInteractEvent event]
               (let [entity (.-target event)
                     player (.-entityPlayer event)
                     world (.-worldObj player)]
                 (if (instance? net.minecraft.entity.passive.EntitySheep entity)
                   (let [properties (get-extended-properties entity "mana-property")]
                     (if (remote? world)
                       (assoc! properties :mana (inc (:mana properties)))
                       (go
                         (<! (timeout 100))
                         (printchat player (str "Sheep Mana: " (:mana properties)))))))))

             (defn trackingEvent [this ^PlayerEvent$StartTracking event]
               (when (or (instance? net.minecraft.entity.player.EntityPlayer (.-target event)) (instance? net.minecraft.entity.passive.EntitySheep (.-target event)))
                 (let [entity (.-target event)
                       mana-property (get-extended-properties entity "mana-property")]
                   (sync-data mana-property))))

             (defn loginEvent [this ^PlayerEvent$PlayerLoggedInEvent event]
               (let [entity (.-player event)
                     mana-property (get-extended-properties entity "mana-property")]
                 (sync-data mana-property))))
