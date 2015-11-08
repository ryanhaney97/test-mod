(ns yoshiquest.test-mod.events
  (:require
   [forge-clj.event :refer [gen-events]]
   [forge-clj.entity :refer [register-extended-properties]]
   [yoshiquest.test-mod.entity-properties :refer [test-properties]]))

;Creates an event handler with the PlayerPickupXpEvent, and the EntityConstructing event.
;The full package names are required.
(gen-events yoshiquest.test-mod.events common-event-handler
            :xpEvent {:event net.minecraftforge.event.entity.player.PlayerPickupXpEvent}
            :entityConstructingEvent {:event net.minecraftforge.event.entity.EntityEvent$EntityConstructing})

;Fires on the PlayerPickupXpEvent, and therefore prints out "Event!" when the player picks up experience.
(defn common-event-handler-xpEvent [this event]
  (println "Event!"))

;Fires on the EntityConstructing event, initializing the new properties for each player.
(defn common-event-handler-entityConstructingEvent [this ^net.minecraftforge.event.entity.EntityEvent$EntityConstructing event]
  (let [entity (.-entity event)]
    (when (and (instance? net.minecraft.entity.player.EntityPlayer entity) (nil? (.getExtendedProperties entity "test-properties")))
      (register-extended-properties entity "test-properties" (.newInstance ^Class test-properties)))))
