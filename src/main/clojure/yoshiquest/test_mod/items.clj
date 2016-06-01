(ns yoshiquest.test-mod.items
  (:require
    [forge-clj.items :refer [defitem deftool defarmor deffood]]
    [forge-clj.util :refer [remote?]])
  (:import
    [net.minecraft.creativetab CreativeTabs]
    [net.minecraft.item ItemFood]
    [net.minecraft.entity.player EntityPlayer]
    [net.minecraft.potion Potion PotionEffect]))

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