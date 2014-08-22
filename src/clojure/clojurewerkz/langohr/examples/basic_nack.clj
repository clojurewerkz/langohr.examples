(ns clojurewerkz.langohr.examples.basic-nack
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.basic     :as lb]
            [langohr.consumers :as lcons]))

(defn consumer1-fn
  [ch {:keys [delivery-tag]} ^bytes payload]
  (when (>= delivery-tag 29)
    (println "Requeueing all previously received messages...")
    (lb/nack ch delivery-tag true true)))

(defn consumer2-fn
  [ch {:keys [delivery-tag]} ^bytes payload]
  (println (format "Consumer 2 got delivery: %d" delivery-tag))
  (lb/ack ch delivery-tag))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        qname (:queue (lq/declare ch "clojurewerkz.langohr.examples.basic-nack.q" {:exclusive true}))]
    (lcons/subscribe ch qname consumer1-fn)
    (lcons/subscribe ch qname consumer2-fn)
    (dotimes [n 30]
      (lb/publish ch "" qname "a message"))
    (Thread/sleep 200)
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
