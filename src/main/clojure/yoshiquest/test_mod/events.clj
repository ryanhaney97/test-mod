(ns yoshiquest.test-mod.events
  (:require
    [forge-clj.registry :refer [register]]
    [forge-clj.event :refer [gen-events]]
    [forge-clj.util :refer [with-prefix]]
    [yoshiquest.test-mod.entity-properties :refer [test-properties mana-property]]
    )
  (:import
    [net.minecraftforge.event.entity EntityEvent$EntityConstructing]
    )
  )

;Creates an event handler with the PlayerPickupXpEvent, and the EntityConstructing event.
;The full package names are required if not using a forge or fml event.
(gen-events common-event-handler
            :xpEvent {:event :player-pickup-xp-event}
            :entityConstructingEvent {:event :entity-event.entity-constructing})

(with-prefix common-event-handler-
             (defn xpEvent [_ _]
               (println "Event!"))

             (defn entityConstructingEvent [this ^EntityEvent$EntityConstructing event]
               (let [entity (.-entity event)]
                 (when (and (instance? net.minecraft.entity.player.EntityPlayer entity) (nil? (.getExtendedProperties entity "test-properties")))
                   (register entity "test-properties" (.newInstance ^Class test-properties))
                   (register entity "mana-property" (.newInstance ^Class mana-property))))))
