(ns yoshiquest.test-mod.blocks
  (:require [forge-clj.blocks :refer [defblock]])
  (:import
    [net.minecraft.block Block]
    [net.minecraft.creativetab CreativeTabs]))

(defblock test-block
          :unlocalized-name "test-block"
          :hardness 0.5
          :creative-tab CreativeTabs/tabBlock
          :light-level (float 1.0)
          :step-sound Block/soundTypeStone)
