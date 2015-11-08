(ns yoshiquest.test-mod.core
  (:require
   [forge-clj.core :refer [defmod]]
   [yoshiquest.test-mod.common :refer [common-pre-init common-init]]
   [yoshiquest.test-mod.client :refer [client-init]]))

;Creates the mod itself, passing in the common-init function as the initializing function for the mod's common proxy.
(defmod yoshiquest.test-mod.core test-mod "0.4.0"
  :common {:init common-init
           :pre-init common-pre-init}
  :client {:init client-init})
