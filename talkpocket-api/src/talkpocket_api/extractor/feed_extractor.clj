(ns talkpocket-api.extractor.feed-extractor
  (:require [diffbot.core :refer :all]))

(def token (System/getenv "DIFFBOT_TOKEN"))

(defn extract
  "Extract article from URL"
  [url]
  (let [content (article token url)
        text (:text content)
        url (:url content)]
    {:text text
     :url url}))
