(ns clojurewerkz.langohr.examples.redelivery
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb])
  (:import [java.util.concurrent TimeUnit ScheduledThreadPoolExecutor Callable]))

(def es (ScheduledThreadPoolExecutor. 4))

(defn periodically
  [n f]
  (.scheduleWithFixedDelay es ^Runnable f 0 n TimeUnit/MILLISECONDS))

(defn after
  [n f]
  (.schedule es ^Callable f n TimeUnit/MILLISECONDS))

(defn start-acking-consumer
  [ch queue id]
  (let [handler (fn [ch {:keys [headers delivery-tag redelivery?]} ^bytes payload]
                  (println (format "%s received a message, i = %d, redelivery? = %s, acking..." id (get headers "i") redelivery?))
                  (lb/ack ch delivery-tag))]
    (lc/subscribe ch queue handler {:auto-ack false})))

(defn start-skipping-consumer
  [ch queue id]
  (let [handler (fn [ch {:keys [headers delivery-tag]} ^bytes payload]
                  (println (format "%s received a message, i = %d" id (get headers "i"))))]
    (lc/subscribe ch queue handler {:auto-ack false})))


(defn -main
  [& args]
  ;; N connections imitate N apps
  (let [conn1    (rmq/connect)
        conn2    (rmq/connect)
        conn3    (rmq/connect)
        ch1      (lch/open conn1)
        ch2      (lch/open conn2)
        chx      (lch/open conn3)
        exchange "amq.direct"
        queue    "langohr.examples.redelivery"]
    (lb/qos ch1 1)
    (lb/qos ch2 1)
    (lq/declare chx queue {:auto-delete true :exclusive false})
    (lq/bind    chx queue exchange {:routing-key "key1"})
    ;; this consumer will ack messages
    (start-acking-consumer   ch1 queue "consumer1")
    ;; this consumer won't ack messages and will "crash" in 4 seconds
    (start-skipping-consumer ch2 queue "consumer2")
    (let [i      (atom 0)
          future (periodically 800 (fn []
                                     (try
                                       (lb/publish chx exchange "key1" "" {:headers {"i" @i}})
                                       (swap! i inc)
                                       (catch Throwable t
                                         (.printStackTrace t)))))]
      (after 4000 (fn []
                    (println "---------------- Shutting down consumer 2 ----------------")
                    (rmq/close ch2)))
      (after 8000 (fn []
                    (println "Shutting down...")
                     (.shutdownNow es)
                     (lq/purge chx queue)
                     (rmq/close ch1)
                     (rmq/close chx)
                     (rmq/close conn1)
                     (rmq/close conn2)
                     (rmq/close conn3))))))
