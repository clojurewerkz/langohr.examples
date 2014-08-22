(ns clojurewerkz.langohr.examples.publisher-confirms
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.confirm   :as lcf]
            [langohr.queue     :as lq]
            [langohr.exchange  :as lx]
            [langohr.basic     :as lb]))

(def ^{:const true}
  default-exchange-name "")

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (doto (lch/open conn)
                (lcf/select))
        q     (lq/declare-server-named ch)]
    (dotimes [n 1000]
      (lb/publish ch default-exchange-name q "msg"))
    (lcf/wait-for-confirms ch)
    (println "All confirms arrived...")
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
