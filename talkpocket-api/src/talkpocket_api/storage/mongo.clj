(ns talkpocket-api.storage.mongo
  (:require [monger.core :as core]
            [monger.collection :as coll]
            [monger.conversion :refer :all]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]])
  (:import org.bson.types.ObjectId))

(def session (delay (core/connect {:host "api.mongo"})))
(def db (delay (core/get-db @session "talkpocket")))
(def collection "entry")

(defn- set-id [input]
  (let [{obj :_id} input
        id (str obj)]
    (conj input {:_id id})))

(defn- keywordize [input]
  (set-id (from-db-object input true)))

(defn save [& [content]]
  (let [to-save (set-id content)]
    (coll/save-and-return @db collection to-save)))

(defn find
  ([] (map keywordize (coll/find-maps @db collection)))
  ([id] (set-id (coll/find-map-by-id @db collection id))))

(defn consumer
  "Consumer that receives a map and persists it to Mongo"
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
          (let [{id :_id} entry]
            (>! out (find id)))
          (= operation "all")
          (>! out (find))
          )))
    out))
