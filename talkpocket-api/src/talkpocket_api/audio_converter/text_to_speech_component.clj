(ns talkpocket-api.audio-converter.text-to-speech-component
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [talkpocket-api.helpers :as helper]))

(import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech)
(import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice)
(import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat)

(def watsonService
  (let [service (new TextToSpeech)
        username (env :watson-user)
        password (env :watson-pwd)]
    (.setUsernameAndPassword service username password)
    service))

(def defaultVoice "en-US_LisaVoice")
(def defaultFormat "audio/wav")

(defn- create-tempory-file [] (str "/tmp/" (helper/uuid) ".wav"))

(defn- convert-text-to-voice
  ([input] (convert-text-to-voice {:text input} defaultVoice))
  ([input voiceType]
   (let [voice (new Voice voiceType nil nil)
         text {:text input}
         in (.execute (.synthesize watsonService text voice (. AudioFormat WAV)))
         filename (create-tempory-file)]
     (io/copy in (io/file filename))
     {:file_id filename})
   ))

(defn- dummy-watson
  [input]
  (let [out (chan)]
    (go
      (println input)
      (>! out (conj input {:file_id (create-tempory-file)})))
    [out]))

(defn consumer [in]
  (let [out (chan)]
    (go
      (let [input (<! in)]
        (>! out (conj (convert-text-to-voice input) input))))
    [out]))

