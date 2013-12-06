(ns clojurewerkz.langohr.examples.recovery.example2
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as lx]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb])
  (:import java.io.IOException
           java.util.UUID
           com.rabbitmq.client.AlreadyClosedException))

(defn to-s
  [^bytes bs]
  (String. bs "UTF-8"))

(defn port
  []
  (if-let [v (System/getenv "PORT")]
    (Integer/valueOf v)
    5672))

(defn -main
  [& args]
  ;; Topology recovery requires Langohr 1.8.0+. MK.
  (let [conn (rmq/connect {:automatically-recover true :automatically-recover-topology true :port (port)})
        ch1  (lch/open conn)
        ch2  (lch/open conn)
        ch3  (lch/open conn)
        x    "langohr.examples.recovery.topic"
        q1   "langohr.examples.recovery.client_named_queue1"
        q2   "langohr.examples.recovery.client_named_queue2"
        q3   "langohr.examples.recovery.client_named_queue3"]
    (println "Connected.")
    (lx/topic  ch1 x  :durable true)
    (lq/declare ch1 q1 :durable true)
    (lq/declare ch2 q2 :durable true)
    (lq/declare ch3 q3 :durable true)
    (lq/bind ch1 q1 x :routing-key "abc")
    (lq/bind ch2 q2 x :routing-key "def")
    (lq/bind ch3 q3 x :routing-key "xyz")
    (println "Initialized the topology")
    (lc/subscribe ch1 q1 (fn [ch meta ^bytes payload]
                           (println (format "[Q1] Consumed %s" (to-s payload))))
                  :auto-ack true)
    (lc/subscribe ch2 q2 (fn [ch meta ^bytes payload]
                           (println (format "[Q2] Consumed %s" (to-s payload))))
                  :auto-ack true)
    (lc/subscribe ch3 q3 (fn [ch meta ^bytes payload]
                           (println (format "[Q3] Consumed %s" (to-s payload))))
                  :auto-ack true)

    (while true
      (Thread/sleep 500)
      (try
        (let [rk (rand-nth ["abc" "def" "xyz" "123"])]
          (println (format "Publishing a message with routing key %s" rk))
          (lb/publish ch1 x rk "hello"))
        (catch AlreadyClosedException ace
          (comment "Happens when you publish while the connection is down"))
        (catch IOException ioe
          (comment "ditto"))))))
