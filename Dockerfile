FROM docker.m.daocloud.io/openjdk:8-alpine3.9

WORKDIR /app
COPY target/sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar .
ENTRYPOINT java -jar sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar