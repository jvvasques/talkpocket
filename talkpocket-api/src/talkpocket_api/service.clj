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
            [talkpocket-api.storage.mongo :as storage]
            [cheshire.core :refer :all]
            [talkpocket-api.storage.minio :as minio]
            [base64-clj.core :as base64]
            [ring.middleware.cors :as cors]
            [talkpocket-api.translation.unbabel :as unbabel]))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

(defn- convert-url-to-talk
  [{:keys [headers params json-params path-params] :as request}]
  (let [{url :url lang :lang} json-params
        id (base64/encode url "UTF-8")
        in (chan)
        extractor-chan (feed/consumer in)
        watson-chan (watson/consumer extractor-chan)
        storage-chan (storage/consumer watson-chan)
        minio-chan (minio/consumer storage-chan)
        change-state-chan (storage/consumer minio-chan)]
    (storage/save {:_id id})
    (>!! in {:url url :_id id :op "insert" :lang lang})
    (ring-resp/response id)))

(defn- list-talks [request]
  (let [in (chan)
        entry-chan (storage/consumer in)]
    (>!! in {:op "all"})
    (ring-resp/response (generate-string (<!! entry-chan)))))

(defn- get-talk [request]
  (let [talk-id (get-in request [:path-params :id])
        in (chan)
        entry-chan (storage/consumer in)]
    (>!! in {:op "search" :_id talk-id})
    (ring-resp/response (generate-string (<!! entry-chan)))))

(defn- get-audio-file [request]
  (let [file-id (get-in request [:path-params :id])
        in-storage (chan)
        in-minio (chan)
        storage-chan (storage/consumer in-storage)
        minio-chan (minio/consumer in-minio)]
    (>!! in-storage {:op "search" :_id file-id})
    (>!! in-minio {:_id (get (<!! storage-chan) :_id) :op "fetch"})
    (ring-resp/content-type (ring-resp/response (<!! minio-chan)) "audio/wav")))

(defroutes routes
  [[["/talk" {:post convert-url-to-talk}
             {:get  list-talks}
             ^:interceptors [(body-params/body-params)]
     ["/:id" {:get get-talk}]]
    ["/file/:id" {:get get-audio-file}]]])

(def service {:env :prod
              ::http/routes routes
              ::http/allowed-origins ["http://localhost:3001" "*"]
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
