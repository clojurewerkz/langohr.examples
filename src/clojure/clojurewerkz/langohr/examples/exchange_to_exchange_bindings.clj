(ns clojurewerkz.langohr.examples.exchange-to-exchange-bindings
  (:gen-class)
  (:require [langohr.core     :as rmq]
            [langohr.channel  :as lch]
            [langohr.queue    :as lq]
            [langohr.exchange :as lx]
            [langohr.basic    :as lb]))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        x1    "clojurewerkz.langohr.examples.dlx.x1"
        x2    "clojurewerkz.langohr.examples.dlx.x2"
        qname "clojurewerkz.langohr.examples.dlx.q"]
    (lx/direct ch x1 {:durable false})
    (lx/fanout ch x2 {:durable false})
    (lq/declare ch qname {:exclusive true})
    (lq/bind ch qname x2)
    (lx/bind ch x2 x1 {:routing-key "unsorted"})
    (lb/publish ch x1 "unsorted" "a message")
    (Thread/sleep 50)
    (println (format "Queue %s has %d message(s)" qname (lq/message-count ch qname)))
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
