(ns talkpocket-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [io.pedestal.http.route.definition :refer :all]
            [talkpocket-api.extractor.feed-extractor :as feed]
            [talkpocket-api.audio-converter.text-to-speech-component :as watson]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [talkpocket-api.helpers :as helper]
            [talkpocket-api.entry.entry-dal :as entry-dal]
            [cheshire.core :refer :all]))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

(defn- convert-url-to-podcast
  [{:keys [headers params json-params path-params] :as request}]
  (let [{url :url} json-params
        in (chan)
        extractor-chan (feed/consumer in)
        watson-chan (watson/consumer extractor-chan)
        entry-dal-chan (entry-dal/consumer watson-chan)
        uuid (helper/uuid)]
    (>!! in {:url url :id uuid :op "insert"})
    (ring-resp/response uuid)))

(defn- get-all-talks [request]
  (let [in (chan)
        entry-chan (entry-dal/consumer in)]
    (>!! in {:op "all"})
    (ring-resp/response (generate-string (<!! entry-chan)))))

(defn- get-talk [request]
  (let [talk-id (get-in request [:path-params :id])
        in (chan)
        entry-chan (entry-dal/consumer in)]
    (>!! in {:op "search" :id talk-id})
    (ring-resp/response (generate-string (first (<!! entry-chan))))))

(defroutes routes
  [[["/talk" {:post convert-url-to-podcast}
             {:get  get-all-talks}
             ^:interceptors [(body-params/body-params)]
     ["/:id" {:get get-talk}]]]])

;; Consumed by talkpocket-api.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})
