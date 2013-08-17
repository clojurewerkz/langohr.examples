(ns clojurewerkz.langohr.examples.queue-ttl
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.shutdown  :as lsh]))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        qname "clojurewerkz.langohr.examples.queue-ttl"]
    (lq/declare ch qname :arguments {"x-expires" 500})
    (Thread/sleep 600)
    (try
      (lq/declare-passive ch qname)
      (catch java.io.IOException ioe
          (let [shutdown-ex (.getCause ioe)
                code        (-> (lsh/reason-of shutdown-ex)
                                .getMethod
                                .getReplyCode)]
            (when (= code 404)
              (println "Queue no longer exists")))))
    (println "[main] Disconnecting...")
    (when (rmq/open? ch)
      (rmq/close ch))
    (rmq/close conn)))
