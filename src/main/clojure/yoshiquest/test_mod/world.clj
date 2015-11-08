(ns yoshiquest.test-mod.world
  (:require
   [forge-clj.world :refer [defgenerate run-default-generator]]
   [yoshiquest.test-mod.blocks :refer [test-block]]))

;A generator function for the testblock.
(defn gen-test-block [world random x z]
  (let [generator (net.minecraft.world.gen.feature.WorldGenMinable. test-block 8)]
    (run-default-generator generator world random x z 20 0 128)))

;The actual generator, passing in gen-testblock as the generation function.
(defgenerate test-gen :overworld gen-test-block)
