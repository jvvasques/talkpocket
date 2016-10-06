(ns talkpocket-api.service.text-to-speech
  (:require [clojure.core.async :as async :refer :all]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [clojure.java.io :as io]
            [environ.core :refer [env]]))

(import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech)
(import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice)
(import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat)

(def watsonService
  (let [service (new TextToSpeech)
        username (env :WATSON_USER)
        password (env :WATSON_PWD)
        ]
    (.setUsernameAndPassword service username password)
    service))

(def defaultVoice "en-US_LisaVoice")
(def defaultFormat "audio/wav")

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn- create-tempory-file [] (str "/tmp/" (uuid) ".wav"))

(defn- convert-text-to-voice
  ([text]  (convert-text-to-voice text defaultVoice))
  ([text voiceType]
   (let [voice (new Voice voiceType nil nil)
         in (.execute (.synthesize watsonService text voice (. AudioFormat WAV)))]
     (io/copy in (io/file (create-tempory-file))))))

