(ns yoshiquest.test-mod.items
  (:require
    [forge-clj.items :refer [defitem deftool defarmor deffood]]
    [forge-clj.util :refer [remote? get-extended-properties printchat]]
    [forge-clj.network :refer [fc-network-send fc-network-receive]]
    [clojure.core.async :refer [chan go >!! <! sub]])
  (:import
    [net.minecraft.creativetab CreativeTabs]
    [net.minecraft.potion Potion]))

(defitem test-item
         :creative-tab CreativeTabs/tabMisc)

(def test-material
  {:name             "test-material"
   :texture-name     "test-mod:test-material"
   :harvest-level    1
   :durability       100
   :mining-speed     4
   :damage           0
   :enchantability   10
   :damage-reduction {:helmet     5
                      :chestplate 7
                      :leggings   4
                      :boots      3}})

(deftool test-shovel :shovel test-material)

(defarmor test-boots :boots (assoc test-material :durability 10))

(deffood test-food 4 0.7
         :potion-effect [(.-id Potion/confusion) 20 0 1.0])

(let [net-sub (sub fc-network-receive :message (chan))]
  (go
    (while true
      (let [nbt-map (<! net-sub)]
        (printchat (:player nbt-map) (str "Server: " (:message nbt-map)))))))

(let [net-sub (sub fc-network-receive :other-message (chan))]
  (go
    (while true
      (let [nbt-map (<! net-sub)]
        (printchat (:player nbt-map) (str "Client: " (:message nbt-map)))))))

;Right click function for the net-test item. Simply sends a message to the server.
(defn right-click-send [istack world player]
  (if (remote? world)
    (>!! fc-network-send {:message "Hello World"
                          :send :server
                          :id :message})
    (>!! fc-network-send {:message "Goodbye World"
                          :send :to
                          :target player
                          :id :other-message}))
  istack)

;Test item to test the network system.
(defitem net-test
         :creative-tab CreativeTabs/tabMisc
         :override {:on-item-right-click right-click-send})

;Right click function for testing the extended entity properties. Prints out the current "tacopower",
;and increases it by 1.
(defn right-click-property [istack world player]
  (when (not (remote? world))
    (let [test-properties (get-extended-properties player "test-properties")]
      (printchat player (str "Tacopower: " (:tacopower test-properties)))
      (assoc! test-properties :tacopower (inc (:tacopower test-properties)))))
  istack)

;Creates an item to test extended entity properties.
(defitem property-test
         :creative-tab CreativeTabs/tabMisc
         :override {:on-item-right-click right-click-property})