FROM docker.m.daocloud.io/openjdk:8-alpine3.9

WORKDIR /app
ARG NACOS_ADDRESS=localhost:8848

WORKDIR /app
ENV JAVA_OPTS ''
COPY target/sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar .
ENTRYPOINT java -jar $JAVA_OPTS sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar
