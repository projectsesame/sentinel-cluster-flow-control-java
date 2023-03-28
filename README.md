## Run:
mvn clean package -Dmaven.test.skip=true

java -jar -DNACOS_ADDRESS=localhost:8848 target/sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar
