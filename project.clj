(defproject clojurewerkz/langohr.examples "1.0.0-SNAPSHOT"
  :description "Various examples for Langohr documentation"
  :min-lein-version "2.4.3"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure     "1.6.0"]
                 [com.novemberain/langohr "3.0.0-rc2"]]
  :source-paths  ["src/clojure"]
  :url "https://github.com/clojurewerkz/langohr.examples"
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :jvm-opts ["-Xmx512m"])
