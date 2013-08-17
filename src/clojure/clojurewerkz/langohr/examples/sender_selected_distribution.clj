(ns clojurewerkz.langohr.examples.sender-selected-distribution
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
        q1    "clojurewerkz.langohr.examples.sender-selected-distribution1"
        q2    "clojurewerkz.langohr.examples.sender-selected-distribution2"
        q3    "clojurewerkz.langohr.examples.sender-selected-distribution3"]
    (lq/declare ch q1 :durable false)
    (lq/declare ch q2 :durable false)
    (lq/declare ch q3 :durable false)
    (lb/publish ch default-exchange-name "won't-route-anywhere" "a message" :headers {"CC" [q2 q3]})
    (Thread/sleep 50)
    (println (format "Queue %s has %d messages" q1 (lq/message-count ch q1)))
    (println (format "Queue %s has %d messages" q2 (lq/message-count ch q2)))
    (println (format "Queue %s has %d messages" q3 (lq/message-count ch q3)))
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
