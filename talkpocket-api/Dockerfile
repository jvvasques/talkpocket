FROM lynxtp/lein
MAINTAINER Your Name <you@example.com>

WORKDIR /app/talkpocket-api

ADD project.clj .
RUN lein deps

ADD . .

EXPOSE 8080

CMD ["./lein", "run"]
