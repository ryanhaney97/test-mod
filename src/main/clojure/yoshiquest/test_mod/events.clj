(ns yoshiquest.test-mod.events
  (:require
    [forge-clj.registry :refer [register]]
    [forge-clj.event :refer [gen-events]]
    [forge-clj.entity :refer [add-extended-properties]]
    [forge-clj.util :refer [with-prefix printchat get-extended-properties remote? sync-data]]
    [clojure.core.async :refer [go timeout <!]]
    [yoshiquest.test-mod.entity :refer [test-properties mana-property]])
  (:import
    [net.minecraftforge.event.entity.player EntityInteractEvent PlayerEvent$StartTracking]
    [net.minecraftforge.fml.common.gameevent PlayerEvent$PlayerLoggedInEvent]))

;Creates an event handler with the PlayerPickupXpEvent, and the EntityConstructing event.
;The full package names are required if not using a forge or fml event.
(gen-events common-event-handler
            :player-pickup-xp-event {:fn (fn [_]
                                           (println "Event!"))}

            :entity-event.entity-constructing {:fn (fn [event]
                                                     (add-extended-properties event [:player] "test-properties" test-properties)
                                                     (add-extended-properties event [:player :sheep] "mana-property" mana-property))}

            :entity-interact-event {:fn (fn [^EntityInteractEvent event]
                                          (let [entity (.-target event)
                                                player (.-entityPlayer event)
                                                world (.-worldObj player)]
                                            (if (instance? net.minecraft.entity.passive.EntitySheep entity)
                                              (let [properties (get-extended-properties entity "mana-property")]
                                                (if (remote? world)
                                                  (assoc! properties :mana (inc (:mana properties)))
                                                  (go
                                                    (<! (timeout 100))
                                                    (printchat player (str "Sheep Mana: " (:mana properties)))))))))}

            :player-event.start-tracking {:fn (fn [^PlayerEvent$StartTracking event]
                                                (let [entity (.-target event)
                                                      player (.-entityPlayer event)]
                                                  (when (or (instance? net.minecraft.entity.player.EntityPlayer entity) (instance? net.minecraft.entity.passive.EntitySheep entity))
                                                    (let [mana-property (get-extended-properties entity "mana-property")]
                                                      (sync-data mana-property player)))))}

            :player-event.player-logged-in-event {:fn (fn [^PlayerEvent$PlayerLoggedInEvent event]
                                                        (let [entity (.-player event)
                                                              mana-property (get-extended-properties entity "mana-property")]
                                                          (sync-data mana-property entity)))})
