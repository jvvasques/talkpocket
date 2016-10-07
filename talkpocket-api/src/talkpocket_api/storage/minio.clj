(ns talkpocket-api.storage.minio)

(import io.minio.MinioClient)
(import io.minio.errors.MinioException)
(import io.minio.ObjectStat)

(def service-host (System/getenv "MINIO_HOST"))
(def service-key (System/getenv "MINIO_KEY"))
(def service-secret (System/getenv "MINIO_SECRET"))
(def bucket "talkpocket")
(def client (new MinioClient service-host service-key service-secret))

(defn create-bucket []
  (if-not (.bucketExists client bucket)
    (.makeBucket client bucket)))

(defn put [name path]
  (.putObject client bucket name path))

(defn get [name]
  (try
    (let []
      (.statObject client bucket name)
      (.getObject client bucket name))
    (catch MinioException e (println e))))
