(ns yoshiquest.test-mod.world
  (:require
   [forge-clj.world :refer [defgenerate run-default-generator defbiome]]
   [forge-clj.util :refer [set-field]]
   [yoshiquest.test-mod.blocks :refer [test-block]]))

;A generator function for the testblock.
(defn gen-test-block [world random x z]
  (let [generator (net.minecraft.world.gen.feature.WorldGenMinable. (.getDefaultState ^net.minecraft.block.Block test-block) 8)]
    (run-default-generator generator world random x z 20 0 128)))

;The actual generator, passing in gen-testblock as the generation function.
(defgenerate test-gen :overworld gen-test-block)

(defbiome test-biome 3
  :class {:expose-fields {spawnableMonsterList {:get getSpawnableMonsterList}
                          spawnableCreatureList {:get getSpawnableCreatureList}}}
  :fields {:min-height (float 0.1)
           :max-height (float 0.3)
           :top-block (.getDefaultState ^net.minecraft.block.Block test-block)
           :filler-block (.getDefaultState ^net.minecraft.block.Block test-block)
           :water-color-multiplier 0x000000}
  :biome-name "Test")

(.clear ^java.util.List (.getSpawnableMonsterList ^TestBiomeClass test-biome))
(.clear ^java.util.List (.getSpawnableCreatureList ^TestBiomeClass test-biome))
