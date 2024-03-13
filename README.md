Zookeeper (soon not needed?)
zookeeper-server-start /usr/local/etc/zookeeper/zoo.cfg

Kafka broker
kafka-server-start /usr/local/etc/kafka/server.properties

Schema registry:
docker run -p 8081:8081 -e SCHEMA_REGISTRY_HOST_NAME=schema-registry \
-e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS="{localhost}:9092" \
-e SCHEMA_REGISTRY_KAFKASTORE_SASL_MECHANISM=PLAIN \
confluentinc/cp-schema-registry:latest

Setup avro schema
Generate Java class from avro.avsc
(TODO: make this step smoother instead of having to drag the generated java class file from the targets folder)

run default with application.properties to see poison pill crazy logs:

kafka-topics --create --topic food_recommendations --bootstrap-server localhost:9092

introduce poison pill!
kafka-console-producer --broker-list localhost:9092 --topic food_recommendations
{"id": 1234, "name": "name", "rating": 4.7}

kafka-topics --bootstrap-server localhost:9092 --delete --topic food_recommendations
(due to props rerunning app automatically will create the topic but here is the command anyways)
kafka-topics --create --topic food_recommendations --bootstrap-server localhost:9092


run with application-antidote.properties
to see how to gracefully handle poison pills




