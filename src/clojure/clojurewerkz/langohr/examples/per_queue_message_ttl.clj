(ns clojurewerkz.langohr.examples.per-queue-message-ttl
  (:gen-class)
  (:require [langohr.core    :as rmq]
            [langohr.channel :as lch]
            [langohr.queue   :as lq]
            [langohr.basic   :as lb]))

(def ^{:const true}
  default-exchange-name "")

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        qname "clojurewerkz.langohr.examples.per-queue-message-ttl"]
    (lq/declare ch qname {:arguments {"x-message-ttl" 500} :durable false})
    (lb/publish ch default-exchange-name qname "a message")
    (Thread/sleep 50)
    (println (format "Queue %s has %d messages" qname (lq/message-count ch qname)))
    (println "Waiting for 600 ms")
    (Thread/sleep 600)
    (println (format "Queue %s has %d messages" qname (lq/message-count ch qname)))
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
