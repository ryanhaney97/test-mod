(ns yoshiquest.test-mod.core
  (:require
   [forge-clj.core :refer [defmod]]
   [yoshiquest.test-mod.common]))

;Creates the mod itself, passing in the common-init function as the initializing function for the mod's common proxy.
(defmod yoshiquest.test-mod.core test-mod "0.4.1"
  :common {:init yoshiquest.test-mod.common/common-init
           :pre-init yoshiquest.test-mod.common/common-pre-init}
  :client {:init yoshiquest.test-mod.client/client-init})