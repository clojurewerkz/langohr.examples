(ns clojurewerkz.langohr.examples.blabbr
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(defn start-consumer
  "Starts a consumer bound to the given topic exchange in a separate thread"
  [ch topic-name username]
  (let [queue-name (format "nba.newsfeeds.%s" username)
        handler    (fn [ch metadata ^bytes payload]
                     (println (format "[consumer] %s received %s" username (String. payload "UTF-8"))))]
    (lq/declare ch queue-name :exclusive false :auto-delete true)
    (lq/bind    ch queue-name topic-name)
    (lc/subscribe ch queue-name handler :auto-ack true)))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        ex    "nba.scores"
        users ["joe" "aaron" "bob"]]
    (le/declare ch ex "fanout" :durable false :auto-delete true)
    (doseq [u users]
      (start-consumer ch "nba.scores" u))
    (lb/publish ch ex "" "BOS 101, NYK 89" :content-type "text/plain" :type "scores.update")
    (lb/publish ch ex "" "ORL 85, ALT 88"  :content-type "text/plain" :type "scores.update")
    (Thread/sleep 2000)
    (rmq/close ch)
    (rmq/close conn)))
