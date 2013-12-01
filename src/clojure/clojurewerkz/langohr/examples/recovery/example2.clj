(ns clojurewerkz.langohr.examples.recovery.example2
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as lx]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb])
  (:import java.io.IOException
           com.rabbitmq.client.AlreadyClosedException))

(defn message-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "[consumer] Received a message: %s"
                   (String. payload "UTF-8")
                   delivery-tag
                   content-type
                   type)))

(defn start-consumer
  [ch ^String q]
  (lq/declare ch q :exclusive false :auto-delete false)
    (lc/subscribe ch q message-handler :auto-ack true))

(defn -main
  [& args]
  ;; Topology recovery requires Langohr 1.8.0+. MK.
  (let [conn (rmq/connect {:automatically-recover true :automatically-recover-topology true})
        ch1  (lch/open conn)
        ch2  (lch/open conn)
        ch3  (lch/open conn)
        ch4  (lch/open conn)
        ch5  (lch/open conn)
        x    (lx/topic "langohr.examples.recovery.topic" :durable true)
        q1   "langohr.examples.recovery.client_named_queue1"
        q2   "langohr.examples.recovery.client_named_queue1"
        q3   "langohr.examples.recovery.client_named_queue1"
        q4   (lq/declare-server-named ch1 :exclusive true)
        q5   (lq/declare-server-named ch2 :exclusive true)
        q6   (lq/declare-server-named ch2 :exclusive true)]
    (println "Connected.")
    (println "Initialized the topology")
    (lc/subscribe ch q1)
    (while true
      (Thread/sleep 1000)
      (try
        (lb/publish ch x q "hello")
        (catch AlreadyClosedException ace
          (comment "Happens when you publish while the connection is down"))
        (catch IOException ioe
          (comment "ditto"))))))
