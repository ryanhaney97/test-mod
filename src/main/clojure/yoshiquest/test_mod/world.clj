(ns yoshiquest.test-mod.world
  (:require
   [forge-clj.world :refer [defgenerate run-default-generator defbiome]]
   [yoshiquest.test-mod.blocks :refer [test-block]])
  (:import
   [net.minecraft.world.gen ChunkProviderGenerate]))

;A generator function for the testblock.
(defn gen-test-block [world random x z]
  (let [generator (net.minecraft.world.gen.feature.WorldGenMinable. test-block 8)]
    (run-default-generator generator world random x z 20 0 128)))

;The actual generator, passing in gen-testblock as the generation function.
(defgenerate test-gen :overworld gen-test-block)

(defbiome test-biome 3
  :class {:expose-fields {spawnableMonsterList {:get getSpawnableMonsterList}
                          spawnableCreatureList {:get getSpawnableCreatureList}}}
  :fields {:root-height (float 0.4)
           :height-variation (float 0.3)
           :top-block test-block
           :filler-block test-block
           :water-color-multiplier 0x000000}
  :biome-name "Test")

(.clear ^java.util.List (.getSpawnableMonsterList ^TestBiomeClass test-biome))
(.clear ^java.util.List (.getSpawnableCreatureList ^TestBiomeClass test-biome))
