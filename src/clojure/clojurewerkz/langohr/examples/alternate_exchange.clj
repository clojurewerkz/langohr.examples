(ns clojurewerkz.langohr.examples.alternate-exchange
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as lx]
            [langohr.basic     :as lb]))

(defn -main
  [& args]
  (let [conn (rmq/connect)
        ch   (lch/open conn)
        x1   "clojurewerkz.langohr.examples.alternate-exchange.x1"
        x2   "clojurewerkz.langohr.examples.alternate-exchange.x2"
        q    (lq/declare-server-named ch)]
    (lx/fanout ch x1 {:durable false})
    (lx/fanout ch x2
               {:durable false
                :arguments {"alternate-exchange" x1}})
    (lq/bind ch q x1)
    (lb/publish ch x2 "_" "a message")
    (Thread/sleep 50)
    (println (format "Queue %s has %d message(s)" q (lq/message-count ch q)))
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
