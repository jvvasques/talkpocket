(ns talkpocket-api.storage.minio
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(import io.minio.MinioClient)
(import io.minio.errors.MinioException)
(import io.minio.ObjectStat)

(def service-host "http://api.s3:9000")
(def service-key (System/getenv "MINIO_ACCESS_KEY"))
(def service-secret (System/getenv "MINIO_SECRET_KEY"))
(def bucket "talkpocket")
(def client (new MinioClient service-host service-key service-secret))

(defn- create-bucket []
  (if-not (.bucketExists client bucket)
    (.makeBucket client bucket)))

(defn- put [name path]
  (.putObject client bucket name path))

(defn- get [name]
  (try
    (let []
      (.statObject client bucket name)
      (.getObject client bucket name))
    (catch MinioException e (println e))))

(defn consumer
  "Consumer that receives requests for storing or fetching files"
  [in]
  (let [out (chan)]
    (go
      (let [request (<! in)
            {operation :op} request]
        (cond
          (= operation "insert")
          (let [{id :id file-id :file_id} request]
            (create-bucket)
            (put id file-id)
            (>! out (conj request {:op "update"})))
           (= operation "fetch")
           (let [{id :id} request]
             (>! out (get id)))
          )))
    out))

