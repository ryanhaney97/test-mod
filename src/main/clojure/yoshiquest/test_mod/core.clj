(ns yoshiquest.test-mod.core
  (:require
    [forge-clj.core :refer [defmod]]))

(defmod test-mod "0.6.0"
        :common {:init yoshiquest.test-mod.common/common-init}
        :client {:init yoshiquest.test-mod.client/client-init})