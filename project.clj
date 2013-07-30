(defproject clojurewerkz/langohr.examples "1.0.0-SNAPSHOT"
  :description "Various examples for Langohr documentation"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure     "1.5.1"]
                 [com.novemberain/langohr "1.1.0"]]
  :source-paths      ["src/clojure"]
  :url "https://github.com/clojurewerkz/langohr.examples"
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :profiles {:hello-world          {:main clojurewerkz.langohr.examples.hello-world}
             :blabbr               {:main clojurewerkz.langohr.examples.blabbr}
             :weathr               {:main clojurewerkz.langohr.examples.weathr}
             :redelivery           {:main clojurewerkz.langohr.examples.redelivery}
             :fanout-routing       {:main clojurewerkz.langohr.examples.fanout-routing}
             :direct-routing       {:main clojurewerkz.langohr.examples.direct-routing}
             :headers-routing      {:main clojurewerkz.langohr.examples.headers-routing}
             :mandatory-publishing {:main clojurewerkz.langohr.examples.mandatory-publishing}}
  :aliases  {"hello-world" ["with-profile" "hello-world"]
             "blabbr"      ["with-profile" "blabbr"]
             "weathr"      ["with-profile" "weathr"]
             "redelivery"  ["with-profile" "redelivery"]
             "fanout-routing"  ["with-profile" "fanout-routing"]
             "direct-routing"  ["with-profile" "direct-routing"]
             "headers-routing" ["with-profile" "headers-routing"]
             "mandatory-publishing"  ["with-profile" "mandatory-publishing"]}
  :jvm-opts ["-Xmx512m"])
