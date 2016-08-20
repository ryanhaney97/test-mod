(ns yoshiquest.test-mod.entity
  (:require
   [forge-clj.entity :refer [defextendedproperties defmob]]
   [yoshiquest.test-mod.network :refer [test-network]]))

;Creates extended properties called test-properties with the field "tacopower"
;(it was the first thing that popped into my head ok), which is initially set to "0".
(defextendedproperties test-properties
  :fields {:tacopower 0})

(defextendedproperties mana-property
  :fields {:mana 0}
  :sync-data [:mana]
  :network test-network)

(defmob test-mob
        :attributes {:max-health 5
                     :movement-speed 0.2}
        :ai [{:priority 0
              :type net.minecraft.entity.ai.EntityAISwimming}
             {:priority 1
              :type net.minecraft.entity.ai.EntityAIPanic
              :args [2.0]}
             {:priority 2
              :type net.minecraft.entity.ai.EntityAIWander
              :args [1.0]}
             {:priority 3
              :type net.minecraft.entity.ai.EntityAIWatchClosest
              :args [net.minecraft.entity.player.EntityPlayer (float 6.0)]}
             {:priority 4
              :type net.minecraft.entity.ai.EntityAILookIdle}
             {:priority 5
              :compatible :all
              :execute? (constantly true)
              :start (fn [entity]
                       nil)
              :continue (constantly false)}])