(ns clojurewerkz.langohr.examples.consumer-cancel-notifications
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as lx]
            [langohr.basic     :as lb]
            [langohr.consumers :as lcons])
  (:import [java.util.concurrent CountDownLatch TimeUnit]))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        q     (lq/declare-server-named ch)
        latch (CountDownLatch. 1)
        on-cancel (fn [consumer-tag]
                    (println (format "Consumer %s has been cancelled" consumer-tag))
                    (.countDown latch))]
    (lcons/subscribe ch q
                     (fn [ch {:keys [delivery-tag]} ^bytes payload]
                       (comment "No op"))
                     {:auto-ack true
                      :handle-cancel-fn on-cancel})
    (lq/delete ch q)
    (.await latch 200 TimeUnit/MILLISECONDS)
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
