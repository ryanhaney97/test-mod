(ns yoshiquest.test-mod.core
  (:require
    [forge-clj.core :refer [defmod]]))


(defmod test-mod "0.6.1"
        :common {:pre-init yoshiquest.test-mod.common/common-pre-init
                 :init yoshiquest.test-mod.common/common-init}
        :client {:pre-init yoshiquest.test-mod.client/client-pre-init
                 :init yoshiquest.test-mod.client/client-init})