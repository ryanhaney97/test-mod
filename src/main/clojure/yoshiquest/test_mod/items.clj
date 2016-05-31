(ns yoshiquest.test-mod.items
  (:require
    [forge-clj.items :refer [defitem deftool defarmor deffood]])
  (:import
    [net.minecraft.creativetab CreativeTabs]))

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

(deffood test-food 4 0.7)