(ns clojurewerkz.langohr.examples.recovery.example1
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
  ;; Topology recovery requires Langohr 2.0+.0+. MK.
  (let [conn (rmq/connect {:automatically-recover true :automatically-recover-topology false})
        ch   (lch/open conn)
        q    "langohr.examples.recovery.example1.q"
        x    ""]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber ch)))
    (start-consumer ch q)
    (rmq/on-recovery ch (fn [ch]
                          (start-consumer ch q)))
    (while true
      (Thread/sleep 1000)
      (try
        (lb/publish ch x q "hello")
        (catch AlreadyClosedException ace
          (comment "Happens when you publish while the connection is down"))
        (catch IOException ioe
          (comment "ditto"))))))
