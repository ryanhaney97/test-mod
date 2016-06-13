(ns yoshiquest.test-mod.network
  (:require
   [forge-clj.network :refer [gen-packet-handler create-network register-message]]
   [forge-clj.util :refer [printchat]]))

;Future declaration for the network created in the pre-init function.
(declare test-mod-server-network)

;Called on the server upon receiving a packet from the client, printing out the received value.
(defn on-server-packet [nbt-map context]
  (let [player (.-playerEntity (.getServerHandler ^net.minecraftforge.fml.common.network.simpleimpl.MessageContext context))]
    (printchat player (str "Server: " nbt-map))))

;Creates a packet handler named "test-mod-server-network-handler" (what a mouthfull), providing on-server-packet
;as the event called upon receiving a packet.
(gen-packet-handler test-mod-server-network-handler on-server-packet)

(defn init-network []
  (def test-mod-server-network (create-network "test-net"))
  (register-message test-mod-server-network test-mod-server-network-handler 0 :server))
