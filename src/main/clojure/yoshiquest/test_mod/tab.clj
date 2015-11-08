(ns yoshiquest.test-mod.tab
  (:require
   [forge-clj.util :refer [deftab get-item]]))

(defn tab-test-mod-item []
  (get-item "test-mod:test-block"))

(deftab tab-test-mod
  :override {:get-tab-icon-item tab-test-mod-item})
