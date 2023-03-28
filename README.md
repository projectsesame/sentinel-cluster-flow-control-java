## Run:
mvn clean package -Dmaven.test.skip=true

java -jar -DNACOS_ADDRESS=localhost:8848 -Dcsp.sentinel.dashboard.server=localhost:8080 target/sentinel-cluster-flow-control-java-0.0.1-SNAPSHOT.jar

## Nacos Config

#### server 端

- server 端通讯配置

data id：cluster-server-transport-config

group：SENTINEL_GROUP

content：

```json
{
  "idleSeconds": 600
}
```

- server 端 namespace 集合配置

data id：cluster-server-namespace-set

group：SENTINEL_GROUP

content：

```json
[
  "appB",
  "clientA",
  "@@DEFAULT_GROUP@@clusterA"
]
```



#### client 端

- client 端通讯配置

data id：clientA-cluster-client-config

group：SENTINEL_GROUP

content：

```json
{
  "requestTimeout": 600
}
```

- client 端 与 server 端关联配置

data id：clientA-cluster-map

group：SENTINEL_GROUP

content：

```json
[
  {
    "machineId": "10.64.0.81@8721",
    "ip": "10.64.0.81",
    "port": 18730,
    "clientSet": ["10.64.0.81@8719", "10.64.0.81@8722"]
  }
]
```

- dashboard 流控规则中开启集群流控配置

data id：clientA-flow-rules

group：SENTINEL_GROUP

content：

```json
[{"app":"clientA","clusterConfig":{"acquireRefuseStrategy":0,"clientOfflineTime":2000,"fallbackToLocalWhenFail":true,"flowId":167999416437031,"resourceTimeout":2000,"resourceTimeoutStrategy":0,"sampleCount":10,"strategy":0,"thresholdType":1,"windowIntervalMs":1000},"clusterMode":true,"controlBehavior":0,"count":10.0,"gmtCreate":1679994164370,"gmtModified":1679994206535,"grade":1,"id":167999416437031,"ip":"10.64.0.81","limitApp":"default","port":8719,"resource":"/sentinel/read","strategy":0},{"app":"clientA","clusterConfig":{"acquireRefuseStrategy":0,"clientOfflineTime":2000,"fallbackToLocalWhenFail":true,"flowId":1679994505608702,"resourceTimeout":2000,"resourceTimeoutStrategy":0,"sampleCount":10,"strategy":0,"thresholdType":1,"windowIntervalMs":1000},"clusterMode":true,"controlBehavior":0,"count":10.0,"gmtCreate":1679994505608,"gmtModified":1679994505608,"grade":1,"id":1679994505608702,"ip":"10.64.0.81","limitApp":"default","port":8719,"resource":"cluster-resource","strategy":0}]
```
