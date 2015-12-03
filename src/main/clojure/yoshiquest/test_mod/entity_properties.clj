(ns yoshiquest.test-mod.entity-properties
  (:require
   [forge-clj.entity :refer [defextendedproperties]]))

;Creates extended properties called test-properties with the field "tacopower"
;(it was the first thing that popped into my head ok), which is initially set to "0".
(defextendedproperties yoshiquest.test-mod.entity-properties test-properties
  :fields {:tacopower 0})
