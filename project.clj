(defproject clojurewerkz/langohr.examples "1.0.0-SNAPSHOT"
  :description "Various examples for Langohr documentation"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure     "1.4.0"]
                 [com.novemberain/langohr "1.0.0-beta4"]]
  :source-paths      ["src/clojure"]
  :url "https://github.com/clojurewerkz/langohr.examples"
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :profiles {:hello-world {:main clojurewerkz.langohr.examples.hello-world}
             :blabbr      {:main clojurewerkz.langohr.examples.blabbr}
             :weathr      {:main clojurewerkz.langohr.examples.weathr}
             :redelivery  {:main clojurewerkz.langohr.examples.redelivery}}
  :aliases  {"hello-world" ["with-profile" "hello-world"]
             "blabbr"      ["with-profile" "blabbr"]
             "weathr"      ["with-profile" "weathr"]
             "redelivery"  ["with-profile" "redelivery"]}
  :jvm-opts ["-Xmx512m"])
