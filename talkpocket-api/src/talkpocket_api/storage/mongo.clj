(ns talkpocket-api.storage.mongo
  (:require [monger.core :as core]
            [monger.collection :as coll]
            [monger.conversion :refer :all]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]])
  (:import org.bson.types.ObjectId))

(def session (delay (core/connect {:host "localhost"})))
(def db (delay (core/get-db @session "talkpocket")))
(def collection "entry")

(defn save [& [content]]
    (coll/save-and-return @db collection content))

(defn- keywordize [input]
  (from-db-object input true))

(defn find
  ([] (map keywordize (coll/find-maps @db collection)))
  ([id] (keywordize (coll/find-map-by-id @db collection (ObjectId. id)))))

(defn consumer
  "Consumer that receives a map and persists it to Cassandra"
  [in]
  (let [out (chan)]
    (go
      (let [entry (<! in)
            {operation :op} entry]
        (cond
          (= operation "insert")
          (>! out (save entry))
          (= operation "update")
          (>! out (save entry))
          (= operation "search")
          (let [{id :id} entry]
            (>! out (conj (find id))))
          (= operation "all")
          (>! out (find))
          )))
    out))
