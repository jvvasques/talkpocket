(ns talkpocket-api.entry.entry-dal
  (:require [qbits.alia :as alia]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(def cluster (alia/cluster {:contact-points ["127.0.0.1"]}))
(def session (alia/connect cluster))
(def insert-entry-query "INSERT INTO articles (article_id, file_id, file_url) VALUES (?, ?, ?)")
(def get-entry-by-id-query "SELECT * FROM articles WHERE article_id = ?")
(def insert-entry-prepared (delay (alia/prepare session insert-entry-query)))
(def get-entry-by-id-prepared (delay (alia/prepare session get-entry-by-id-query)))

(defn- create-schema []
  (alia/execute session "CREATE KEYSPACE IF NOT EXISTS talkpocket
                         WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3};")
  (alia/execute session "USE talkpocket;")
  (alia/execute session "CREATE TABLE IF NOT EXISTS articles (
                                                article_id varchar,
                                                file_id varchar,
                                                file_url varchar,
                                                PRIMARY KEY (article_id));"))
(defn- insert-entry
  [article-id file-id file_url]
  (alia/execute session "USE talkpocket;")
  (alia/execute session @insert-entry-prepared {:values [article-id file-id file_url]}))

(defn- get-entry
  [article-id]
  (alia/execute session "USE talkpocket;")
  (alia/execute session @get-entry-by-id-prepared {:values [article-id]}))

(defn consumer
  "Consumer that receives a map and persists it to Cassandra"
  [in]
  (let [out (chan)]
    (go
      (let [entry (<! in)
            {operation :op} entry]
        (cond
          (= operation "insert")
           (let [{artcile-id :id file-id :file_id  file-url :url} entry]
            (create-schema)
            (insert-entry artcile-id file-id file-url)
            (>! out entry))
           (= operation "search")
           (let [{article-id :id} entry]
             (>! out (get-entry article-id)))
          )))
    out))

