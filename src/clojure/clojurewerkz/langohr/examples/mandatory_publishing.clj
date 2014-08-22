(ns clojurewerkz.langohr.examples.mandatory-publishing
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(def ^{:const true}
  default-exchange-name "")

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        qname (str (java.util.UUID/randomUUID))
        rl    (lb/return-listener (fn [reply-code reply-text exchange routing-key properties body]
                                    (println "Message returned. Reply text: " reply-text)))]
    (.addReturnListener ch rl)
    (lb/publish ch default-exchange-name qname "Hello!" {:content-type "text/plain" :mandatory true})
    (Thread/sleep 1000)
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
