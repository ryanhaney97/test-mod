(ns yoshiquest.testmod
  (:require [forge-clj.core :refer :all]))

(defitem testitem
  :unlocalized-name "testitem"
  :creative-tab (creative-tab :misc)
  :texture-name "test-mod:testitem")

(defblock testblock
  :block-name "testblock"
  :override {:get-item-dropped (fn [_ _ _] testitem)}
  :hardness 0.5
  :step-sound (step-sound :stone)
  :creative-tab (creative-tab :block)
  :light-level (float 1.0)
  :block-texture-name "test-mod:testblock")

(defn common-init [event]
  (register testblock "testblock")
  (register testitem "testitem")
  (addrecipe testblock {:layout
                        "###
                        #_#
                        ###"
                        :bindings {\# testitem}}))

(defmod yoshiquest.testmod test-mod "0.1.0"
  :common {:init common-init})
