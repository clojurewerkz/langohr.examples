(ns clojurewerkz.langohr.examples.headers-routing
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
                                   (String. payload "UTF-8"))))
        thread  (Thread. (fn []
                           (lc/subscribe ch queue-name handler :auto-ack true)))]
    (.start thread)))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        ename "langohr.examples.headers"]
    (le/declare ch ename "headers")
    (let [qname (.getQueue (lq/declare ch "" :auto-delete true :exclusive false))]
      (lq/bind ch qname ename :arguments {"os" "linux" "cores" 8 "x-match" "all"})
      (start-consumer ch qname))
    (let [qname (.getQueue (lq/declare ch "" :auto-delete true :exclusive false))]
      (lq/bind ch qname ename :arguments {"os" "osx" "cores" 4 "x-match" "any"})
      (start-consumer ch qname))    
    (lb/publish ch ename "" "8 cores/Linux" :content-type "text/plain" :headers {"os" "linux" "cores" 8})
    (lb/publish ch ename "" "8 cores/OS X"  :content-type "text/plain" :headers {"os" "osx"   "cores" 8})
    (lb/publish ch ename "" "4 cores/Linux" :content-type "text/plain" :headers {"os" "linux" "cores" 4})
    (Thread/sleep 2000)
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
