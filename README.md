Zookeeper (soon not needed?)
zookeeper-server-start /usr/local/etc/zookeeper/zoo.cfg

Kafka broker
kafka-server-start /usr/local/etc/kafka/server.properties

Schema registry:
docker run -p 8081:8081 -e SCHEMA_REGISTRY_HOST_NAME=schema-registry \
-e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS="10.58.10.106:9092" \
-e SCHEMA_REGISTRY_KAFKASTORE_SASL_MECHANISM=PLAIN \
confluentinc/cp-schema-registry:latest

Setup avro schema
Generate Java class from avro.avsc
(TODO: make this step smoother instead of having to drag the generated java class file from the targets folder)

kafka-topics --create --topic food_recommendations --bootstrap-server localhost:9092


run default with application.properties to see poison pill crazy logs:


introduce poison pill!
kafka-console-producer --broker-list localhost:9092 --topic food_recommendations
{"id": 1234, "name": "name", "rating": 4.7}

solution 1 - delete topic and recreate:
kafka-topics --bootstrap-server localhost:9092 --delete --topic food_recommendations
restart the application for solution to take place
(due to props rerunning app automatically will create the topic but here is the command anyways)
kafka-topics --create --topic food_recommendations --bootstrap-server localhost:9092

solution 2 - skip offset:
find the value of the CURRENT-OFFSET, the new offset value should be CURRENT-OFFSET + 1
kafka-consumer-groups --bootstrap-server localhost:9092 --group group1 --describe
stop the consumer (i.e. stop the service from running) - since we only have 1 consumer group
skip the offset with following command
kafka-consumer-groups --bootstrap-server localhost:9092 --group group1 --topic food_recommendations --reset-offsets --to-offset <CURRENT-OFFSET + 1> --execute
restart the app

solution 3 - adjust retention period
default is 604800000 ms which is 7 days
kafka-configs --zookeeper localhost:2181 --entity-type topics --entity-name food_recommendations --alter --add-config retention.ms=5
1
kafka-configs --bootstrap-server localhost:9092 --alter --entity-type topics --entity-name food_recommendations --add-config retention.ms=5


run with application-antidote.properties
-Dspring.profiles.active=antidote
to see how to gracefully handle poison pills

Detailed explanation here:
https://github.com/tshalaa1/kafka-poison-pill

postman request to test:
curl --location --request POST 'http://localhost:9090/produce' \
--header 'Content-Type: application/json' \
--data-raw '{
"id": 1234,
"name" : "name2",
"rating" : 1.2
}'




