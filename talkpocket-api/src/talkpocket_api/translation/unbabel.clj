(ns talkpocket-api.translation.unbabel
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(def url "https://sandbox.unbabel.com/tapi/v2/translation/")
(def username (System/getenv "UNBABEL_USERNAME"))
(def apikey (System/getenv "UNBABEL_API_KEY"))
(def headers {"Authorization" (str "ApiKey " username ":" apikey) "Content-Type" "application/json"})

(defn- request-translation
  [text target-lang]
  (let [request-body (str "{\"text\" : \"" text "\", \"target_language\" : \"" target-lang "\", \"text_format\" : \"text\"}")
        content (client/post url
                             {:headers      headers
                              :content-type :json
                              :body         request-body})
        body (:body content)]
    (get (parse-string body true) :uid)))

(defn- get-translation
  [translation-uid]
  (let [translation-url (str url translation-uid)
        content (client/get translation-url {:headers headers})
        body (:body content)
        translated (get (parse-string body true) :translatedText)]
    (if-not (nil? translated)
      translated
      nil)))

(defn- retry-translation
  [translation-uid]
  (loop [uid translation-uid]
    (let [translation (get-translation uid)]
      (Thread/sleep 1000)
      (if-not (nil? translation)
        translation
        (recur translation-uid)))))

(defn- consume-translation-request
  [entry out]
  (let [{operation :op} entry]
    (cond
      (= operation "insert")
      (let [{text :text target-lang :lang} entry
            translation-uid (request-translation text target-lang)
            translated-text (retry-translation translation-uid)]
        (>!! out (conj entry {:text translated-text}))))))

(defn consumer
  "Consumer that provides translation features"
  [in]
  (let [out (chan)]
    (go
      (let [entry (<! in)
            {lang :lang} entry]
        (if (nil? lang)
          (do
            (println "no translation...")
            (>! out entry))
          (consume-translation-request entry out))))
    out))

