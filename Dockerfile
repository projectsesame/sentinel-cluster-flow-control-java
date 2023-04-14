FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY ./  ./
RUN ./mvnw clean package -DskipTests

FROM docker.m.daocloud.io/openjdk:8-alpine3.9

WORKDIR /app
ARG NACOS_ADDRESS=localhost:8848

WORKDIR /app
ENV JAVA_OPTS ''
COPY --from=builder /app/target/sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar .
ENTRYPOINT java -jar $JAVA_OPTS sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar
