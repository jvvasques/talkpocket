#API Endpoints

####Convert URL do podcast
Requests the server to convert an article to an audio talk

* **Endpoint:** /talk
* **Method:** POST
* Body: ```{"url": "http://....", "lang"="pt"} ```
    * `lang` is optional
    
#### List all talks
Returns the list of talks

* **Endpoint:** /talk
* **Method:** GET
* **Response**: 
    ```
    [{"id":"cb45a1f4-b088-4456-a4c0-43bbee38d5c8","audio_format":":wav","file_id":"/tmp/acba6538-a05f-4b99-aa7b-368e4c5eb3cb.wav","file_url":"https://techcrunch.com/2012/05/22/talkdesk/","state":0}]
    ````

#### Get a talk
Returns a talk by id

* **Endpoint:** /{id}
* **Method:** GET
* **Response**:
    ```
    {"id":"cb45a1f4-b088-4456-a4c0-43bbee38d5c8","audio_format":":wav","file_id":"/tmp/acba6538-a05f-4b99-aa7b-368e4c5eb3cb.wav","file_url":"https://techcrunch.com/2012/05/22/talkdesk/","state":0}
    ```

#### Get the audio file for a talk
Returns a talk by id

* **Endpoint:** /file/{file_id}
* **Method:** GET
