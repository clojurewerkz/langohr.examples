(ns clojurewerkz.langohr.examples.dead-letter-exchange
  (:gen-class)
  (:require [langohr.core     :as rmq]
            [langohr.channel  :as lch]
            [langohr.queue    :as lq]
            [langohr.exchange :as lx]
            [langohr.basic    :as lb]))

(def ^{:const true}
  default-exchange-name "")

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        q1    "clojurewerkz.langohr.examples.dlx.q1"
        q2    "clojurewerkz.langohr.examples.dlx.q2"
        dlx   "clojurewerkz.langohr.examples.dlx"]
    (lq/declare ch q1 {:durable false :arguments {"x-dead-letter-exchange" dlx
                                                  "x-message-ttl" 300}})
    (lq/declare ch q2 {:durable false})
    (lx/fanout ch dlx {:durable false})
    (lq/bind ch q2 dlx)
    (lb/publish ch default-exchange-name q1 "a message")
    ;; expired messages are dead lettered
    (Thread/sleep 450)
    (println (format "Queue %s has %d messages" q1 (lq/message-count ch q1)))
    (println (format "Queue %s has %d messages" q2 (lq/message-count ch q2)))
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
