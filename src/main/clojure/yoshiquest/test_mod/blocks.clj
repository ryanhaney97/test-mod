(ns yoshiquest.test-mod.blocks
  (:require
    [forge-clj.blocks :refer [defblock defitemblock get-state]]
    [forge-clj.util :refer [itemstack]])
  (:import
    [net.minecraft.block Block]
    [net.minecraft.creativetab CreativeTabs]
    [net.minecraft.item ItemBlock ItemStack]))

(defblock test-block
          :unlocalized-name "test-block"
          :hardness 0.5
          :creative-tab CreativeTabs/tabBlock
          :light-level (float 1.0)
          :step-sound Block/soundTypeStone)

(defblock meta-block
          :unlocalized-name "meta-block"
          :hardness 0.5
          :creative-tab CreativeTabs/tabBlock
          :step-sound Block/soundTypeCloth
          :state {:color [:black :red :green :blue :yellow :purple]}
          :override {:get-sub-blocks (fn [item tab ^java.util.List list]
                                       (dorun (map #(.add list (itemstack item 1 %1)) (range 6))))
                     :damage-dropped (fn [state]
                                       (.getMetaFromState ^Block this state))})

(defitemblock meta-block-item meta-block
              :max-damage 0
              :has-subtypes true
              :override {:get-metadata (fn [istack]
                                         (if (instance? ItemStack istack)
                                           (.getItemDamage ^ItemStack istack)
                                           istack))
                         :get-unlocalized-name (fn [items]
                                                 (let [this ^ItemBlock this]
                                                   (str (proxy-super getUnlocalizedName items) "-" (name (:color (get-state meta-block items))))))})