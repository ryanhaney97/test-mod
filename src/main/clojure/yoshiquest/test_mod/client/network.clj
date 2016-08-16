(ns yoshiquest.test-mod.client.network
  (:require
    [forge-clj.client.network :refer [defclientnetwork]]
    [yoshiquest.test-mod.network :refer [test-network]]))

(defclientnetwork test-network)