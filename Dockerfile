FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app/

COPY ./  ./
RUN ./mvnw --batch-mode clean package -DskipTests

# -----------------------------------------------------------------------------

FROM docker.m.daocloud.io/library/openjdk:11.0.15-jre

WORKDIR /app/
ARG NACOS_ADDRESS=localhost:8848

COPY --from=builder /app/target/sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar ./

ENTRYPOINT java -jar -DNACOS_ADDRESS=$NACOS_ADDRESS sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar
