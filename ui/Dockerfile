FROM node:6.7

ENV WORK_DIR /app

WORKDIR $WORK_DIR

ADD package.json $WORK_DIR
RUN npm install 

EXPOSE 3000
EXPOSE 3001

ADD . $WORK_DIR

CMD npm start