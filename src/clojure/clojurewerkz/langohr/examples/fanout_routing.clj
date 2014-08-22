(ns clojurewerkz.langohr.examples.fanout-routing
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.exchange  :as le]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))


(defn start-consumer
  "Starts a consumer in a separate thread"
  [ch queue-name]
  (let [handler (fn [ch metadata ^bytes payload]
                  (println (format "[consumer] %s received a message: %s"
                                   queue-name
                                   (String. payload "UTF-8"))))]
    (lc/subscribe ch queue-name handler {:auto-ack true})))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        ename "langohr.examples.fanout"]
    (le/declare ch ename "fanout")
    (dotimes [i 10]
      (let [q (:queue (lq/declare ch "" {:exclusive false :auto-delete true}))]
        (lq/bind    ch q ename)
        (start-consumer ch q)))
    (lb/publish ch ename "" "Ping" {:content-type "text/plain"})
    (Thread/sleep 2000)
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
