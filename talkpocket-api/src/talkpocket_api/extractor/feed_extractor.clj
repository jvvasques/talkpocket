(ns talkpocket-api.extractor.feed-extractor
  (:require [diffbot.core :refer :all]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [environ.core :refer [env]]))

(def token (env :diffbot-token))

(defn- extract
  "Extract article from URL"
  [input]
  (let [{url :url id :_id} input
        content (article token url)
        text (:text content)
        url (:url content)]
    (conj {:text text} input)))

(defn consumer
  "Consumes an url and returns the article from it in the out channel"
  [in]
  (let [out (chan)]
    (go
      (let [url (<! in)]
        (>! out (conj (extract url) url))))
    out))
