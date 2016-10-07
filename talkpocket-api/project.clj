(defproject talkpocket-api "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [io.pedestal/pedestal.service "0.5.1"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.1"]
                 ;; [io.pedestal/pedestal.immutant "0.5.1"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.1"]

                 [org.clojure/core.async "0.2.391"]

                 [cc.qbits/alia-all "3.2.0"]

                 [ch.qos.logback/logback-classic "1.1.7" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.21"]
                 [org.slf4j/jcl-over-slf4j "1.7.21"]
                 [org.slf4j/log4j-over-slf4j "1.7.21"]
                 [diffbot/diffbot "0.1.0"]
                 [com.ibm.watson.developer_cloud/java-sdk "3.4.0"]
                 [environ "1.1.0"]
                 [cc.qbits/hayt "3.0.1"]
                 [cheshire "5.6.3"]
                 [org.slf4j/log4j-over-slf4j "1.7.21"]
                 [io.minio/minio "2.0.3"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.3"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "talkpocket-api.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.1"]]}
             :uberjar {:aot [talkpocket-api.server]}}
  :main ^{:skip-aot true} talkpocket-api.server)
