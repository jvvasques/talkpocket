(ns talkpocket-api.storage.cassandra
  (:require [qbits.alia :as alia]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [qbits.hayt :refer :all]
            ))

(def cluster (alia/cluster {:contact-points ["api.cassandra"]}))
(def session (alia/connect cluster))
(def insert-entry-query "INSERT INTO articles (id, file_id, file_url, audio_format, state) VALUES (?, ?, ?, ?, ?)")
(def update-entry-state-query "UPDATE articles SET state = ? WHERE id = ?")
(def get-entry-by-id-query "SELECT * FROM articles WHERE id = ?")
(def insert-entry-prepared (delay (alia/prepare session insert-entry-query)))
(def update-entry-state-prepared (delay (alia/prepare session update-entry-state-query)))
(def get-entry-by-id-prepared (delay (alia/prepare session get-entry-by-id-query)))

(defn create-schema []
  (alia/execute session "CREATE KEYSPACE IF NOT EXISTS talkpocket
                         WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3};")
  (alia/execute session "USE talkpocket;")
  (alia/execute session "CREATE TABLE IF NOT EXISTS articles (
                                                id varchar,
                                                file_id varchar,
                                                file_url varchar,
                                                audio_format varchar,
                                                state int,
                                                PRIMARY KEY (id));"))
(defn- insert-entry
  [id file-id file_url audio-format state]
  (alia/execute session "USE talkpocket;")
  (alia/execute session @insert-entry-prepared {:values [id file-id file_url audio-format state]}))

(defn- update-article-state
  [id new-state]
  (alia/execute session "USE talkpocket;")
  (alia/execute session @update-entry-state-prepared {:values [id (int new-stat)]}))

(defn- get-entry
  [id]
  (alia/execute session "USE talkpocket;")
  (alia/execute session @get-entry-by-id-prepared {:values [id]}))

(defn- get-all []
  (alia/execute session "USE talkpocket;")
  (alia/execute session (select :articles)))

(defn consumer
  "Consumer that receives a map and persists it to Cassandra"
  [in]
  (let [out (chan)]
    (go
      (let [entry (<! in)
            {operation :op} entry]
        (cond
          (= operation "insert")
           (let [{id :id file-id :file_id file-url :url audio-format :format} entry]
             (insert-entry id file-id file-url (str audio-format) (int 0))
             (>! out entry))
           (= operation "update")
           (let [{id :id} entry]
             (update-article-state 1 id))
           (= operation "search")
           (let [{id :id} entry]
             (>! out (conj (get-entry id))))
           (= operation "all")
           (>! out (get-all))
          )))
    out))
