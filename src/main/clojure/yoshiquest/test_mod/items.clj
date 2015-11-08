(ns yoshiquest.test-mod.items
  (:require
   [forge-clj.items :refer [defitem deftoolmaterial defarmormaterial deftool defarmor deffood]]
   [forge-clj.entity :refer [get-extended-properties]]
   [forge-clj.network :refer [send-to-server]]
   [forge-clj.util :refer [remote?]]
   [yoshiquest.test-mod.network :refer [test-mod-server-network]]
   [yoshiquest.test-mod.tab :refer [tab-test-mod]]))

(defitem test-item
  :unlocalized-name "testitem"
  :creative-tab tab-test-mod
  :texture-name "test-mod:test-item")

;Right click function for the net-test item. Simply sends a message to the server.
(defn right-click-send [istack world player]
  (if (remote? world)
    (send-to-server test-mod-server-network {:message "Hello World"}))
  istack)

;Test item to test the network system.
(defitem net-test
  :unlocalized-name "net-test"
  :creative-tab tab-test-mod
  :override {:on-item-right-click right-click-send})

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
  :creative-tab tab-test-mod
  :override {:on-item-right-click right-click-property})

;Defines a tool material with the name testite and its respective properties.
(deftoolmaterial testite 3 1000 15.0 4.0 30)

;Creates a shovel using the testite material.
(deftool test-shovel testite :shovel
  :unlocalized-name "test-shovel"
  :creative-tab tab-test-mod)

;Creates an armor material for testite with these properties.
(defarmormaterial testite-armor 16 {:helmet 3
                                    :chestplate 8
                                    :leggings 6
                                    :boots 3} 30)

;Creates a pair of test boots.
(defarmor test-boots testite-armor :boots
  :unlocalized-name "test-boots"
  :creative-tab tab-test-mod)

;Creates a simple food item for testing.
(deffood test-nom 5 0.8
  :unlocalized-name "test-nom"
  :creative-tab tab-test-mod)
