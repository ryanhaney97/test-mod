(ns yoshiquest.test-mod.network
  (:require
   [forge-clj.network :refer [gen-packet-handler create-network register-message]]))

;Future declaration for the network created in the pre-init function.
(declare test-mod-server-network)

;Called on the server upon receiving a packet from the client, printing out the received value.
(defn on-server-packet [nbt-map context]
  (println (str "Server: " nbt-map)))

;Creates a packet handler named "test-mod-server-network-handler" (what a mouthfull), providing on-server-packet
;as the event called upon receiving a packet.
(gen-packet-handler yoshiquest.test-mod.network test-mod-server-network-handler on-server-packet)

(defn init-network []
  (def test-mod-server-network (create-network "test-net"))
  (register-message test-mod-server-network test-mod-server-network-handler 0 :server))
