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
            [talkpocket-api.storage.cassandra :as cassandra]
            [cheshire.core :refer :all]
            [talkpocket-api.storage.minio :as minio]
            [base64-clj.core :as base64]
            [ring.middleware.cors :as cors]))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

(defn- convert-url-to-talk
  [{:keys [headers params json-params path-params] :as request}]
  (let [{url :url} json-params
        in (chan)
        extractor-chan (feed/consumer in)
        watson-chan (watson/consumer extractor-chan)
        cassandra-chan (cassandra/consumer watson-chan)
        minio-chan (minio/consumer cassandra-chan)
        enconded (base64/encode url "UTF-8")]
    (>!! in {:url url :id enconded :op "insert"})
    (ring-resp/response enconded)))

(defn- list-talks [request]
  (let [in (chan)
        entry-chan (cassandra/consumer in)]
    (>!! in {:op "all"})
    (ring-resp/response (generate-string (<!! entry-chan)))))

(defn- get-talk [request]
  (let [talk-id (get-in request [:path-params :id])
        in (chan)
        entry-chan (cassandra/consumer in)]
    (>!! in {:op "search" :id talk-id})
    (ring-resp/response (generate-string (first (<!! entry-chan))))))

(defn- get-audio-file [request]
  (let [file-id (get-in request [:path-params :id])
        in-cassandra (chan)
        in-minio (chan)
        cassandra-chan (cassandra/consumer in-cassandra)
        minio-chan (minio/consumer in-minio)]
    (>!! in-cassandra {:op "search" :id file-id})
    (>!! in-minio {:id (get (first (<!! cassandra-chan)) :id) :op "fetch"})
    ; TODO Support other formats and remove hardcoded type
    (ring-resp/content-type (ring-resp/response (<!! minio-chan)) "audio/wav")))

(defroutes routes
  [[["/talk" {:post convert-url-to-talk}
             {:get  list-talks}
             ^:interceptors [(body-params/body-params)]
     ["/:id" {:get get-talk}]]
    ["/file/:id" {:get get-audio-file}]]])

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

              ::http/allowed-origins ["http://localhost:3001"]

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
