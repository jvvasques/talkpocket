# talkpocket-api

FIXME

## Getting Started

1. Set the environment variables:
    ```
    export MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F
    export MINIO_HOST=localhost
    export MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG
    export UNBABEL_USERNAME=<Your username>
    export UNBABEL_API_KEY=<Your API Key>
    ```
1. Start the servers: `docker-compose run`
1. Start the application: `lein run`
1. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
1. Read your app's source code at src/talkpocket_api/service.clj. Explore the docs of functions
   that define routes and responses.
1. Run your app's tests with `lein test`. Read the tests at test/talkpocket_api/service_test.clj.
1. Learn more! See the [Links section below](#links).


## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


## Developing your service

1. Start a new REPL: `lein repl`
1. Start your service in dev-mode: `(def dev-serv (run-dev))`
1. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Build an uberjar of your service: `lein uberjar`
1. Build a Docker image: `sudo docker build -t talkpocket-api .`
1. Run your Docker image: `docker run -p 8080:8080 talkpocket-api`

### [OSv](http://osv.io/) unikernel support with [Capstan](http://osv.io/capstan/)

1. Build and run your image: `capstan run -f "8080:8080"`

Once the image it built, it's cached.  To delete the image and build a new one:

1. `capstan rmi talkpocket-api; capstan build`


## Links
* [Other examples](https://github.com/pedestal/samples)

