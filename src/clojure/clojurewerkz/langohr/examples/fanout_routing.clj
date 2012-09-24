(ns clojurewerkz.langohr.examples.fanout-routing
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.exchange  :as le]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(defn message-handler
  [ch metadata ^bytes payload]
  (println (format "[consumer] Received a message: %s"
                   (String. payload "UTF-8"))))

(defn start-consumer
  "Starts a consumer in a separate thread"
  [ch queue-name]
  (let [thread (Thread. (fn []
                          (lc/subscribe ch queue-name message-handler :auto-ack true)))]
    (.start thread)))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        qname "langohr.examples.fanout-routing"
        ename "langohr.examples.fanout"]
    (lq/declare ch qname :exclusive false :auto-delete true)
    (start-consumer ch qname)
    (le/declare ch ename "fanout")
    (lq/bind    ch qname ename)
    (lb/publish ch ename qname "Ping" :content-type "text/plain")
    (Thread/sleep 2000)
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
