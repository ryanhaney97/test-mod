(ns yoshiquest.test-mod.network
  (:require
    [clojure.core.async :refer [go-loop sub chan alts!]]
    [forge-clj.network :refer [defnetwork fc-network-receive]]))

(defnetwork test-network)

;(let [to-sub (sub fc-network-receive :send-to (chan))
;      around-sub (sub fc-network-receive :send-around (chan))
;      all-sub (sub fc-network-receive :send-all (chan))
;      server-sub (sub fc-network-receive :send-server (chan))]
;  (go-loop [data (alts! [to-sub around-sub all-sub server-sub])]
;           (println (str "Message caught in wrong network: " data))
;           (recur (alts! [to-sub around-sub all-sub server-sub]))))