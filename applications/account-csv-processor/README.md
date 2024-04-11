# account-csv-processor

A simple developer defined function to convert CSV data to a domain object.

-----------------------

# Location Testing

## Start GemFire

```shell
./deployments/local/scripts/gemfire/start.sh
```

[Setup and Install SCDF](https://dataflow.spring.io/docs/installation/local/manual/)

------------------------
## Start RabbitMQ

See [RabbitMQ documentation](https://www.rabbitmq.com/docs/download)

##  Start SCDF

Start Skipper
```shell
java -jar $SCDF_HOME/spring-cloud-skipper-server-2.11.2.jar  
```

Start DataFlow
```shell
java -jar $SCDF_HOME/spring-cloud-dataflow-server-2.11.2.jar --server.port=9393  --spring.cloud.dataflow.features.skipper-enabled=true 
```

Access Dashboard

```shell
open http://localhost:9393/dashboard
```

Goto Click [Apps](http://localhost:9393/dashboard/index.html#/apps) ->[Add](http://localhost:9393/dashboard/index.html#/apps/add)

- Click App as Properties

Paste the following with the locations of GemFire SCDF and custom apps

```properties
sink.gemfire=file:///Users/devtools/repositories/IMDG/gemfire/scdf/apps/gemfire-sink-rabbit-1.0.0.jar
sink.gemfire.metadata=file:///Users/devtools/repositories/IMDG/gemfire/scdf/apps/gemfire-sink-rabbit-1.0.0-metadata.jar
source.account-file=file:///Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/spring-gemfire-showcase/applications/account-file-supplier/target/account-file-supplier-0.0.1-SNAPSHOT.jar
processor.account-csv-json=file:///Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/spring-gemfire-showcase/applications/account-csv-processor/target/account-csv-processor-0.0.1-SNAPSHOT.jar
```

Note: please build the custom apps and download the GemFire for Spring Cloud DataFlow applications


## Create Stream Pipeline

- Click [Streams](http://localhost:9393/dashboard/index.html#/streams/list) ->  [Create](http://localhost:9393/dashboard/index.html#/streams/list/create)
- Paste the following example

```shell
account-file --file.directory="/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/spring-gemfire-showcase/applications/account-csv-processor/src/test/resources/csv/account/"  | account-csv-json | gemfire --gemfire.region.regionName=Account --gemfire.consumer.keyExpression="payload.getField('id')" --gemfire.consumer.json=true --gemfire.pool.host-addresses="localhost:10334" --spring.rabbitmq.host=localhost
```

Click Create stream(s)
- type name
- Click the ":" next to the stream 
- Click Deploy -> Deploy Stream 