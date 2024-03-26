### Zookeeper (soon not needed?)
```bash
zookeeper-server-start /usr/local/etc/zookeeper/zoo.cfg
```

### Kafka Broker
```bash
kafka-server-start /usr/local/etc/kafka/server.properties
```

### Schema Registry
```bash
docker run -p 8081:8081 -e SCHEMA_REGISTRY_HOST_NAME=schema-registry \
-e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS="{localhost}:9092" \
-e SCHEMA_REGISTRY_KAFKASTORE_SASL_MECHANISM=PLAIN \
confluentinc/cp-schema-registry:latest
```

### Setup Avro Schema and Run the app
```bash
mvn clean
```

```bash
mvn avro:schema
```

```bash
mvn install
```
The generated Java class will be generated inside the target folder

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=antidote
```
```bash
mvn spring-boot:run
```

### Create Kafka Topic
```bash
kafka-topics --create --topic payment_requests --bootstrap-server localhost:9092
```

### Run Default with Application Properties
To see poison pill logs:
- Introduce poison pill!
  ```bash
  kafka-console-producer --broker-list localhost:9092 --topic payment_requests
  {"id": 1234, "type": "name", "amount": 4.7, "currency": "EUR"}
  ```

### Solutions to Handle Poison Pill (without antidote)

#### Solution 1 - Delete Topic and Recreate
- Delete the topic and recreate:
  ```bash
  kafka-topics --bootstrap-server localhost:9092 --delete --topic payment_requests
  ```
- Restart the application for the solution to take place.

#### Solution 2 - Skip Offset
- Find the value of the CURRENT-OFFSET. The new offset value should be CURRENT-OFFSET + 1.
  ```bash
  kafka-consumer-groups --bootstrap-server localhost:9092 --group group1 --describe
  ```
- Stop the consumer (i.e., stop the service from running).
- Skip the offset with the following command:
  ```bash
  kafka-consumer-groups --bootstrap-server localhost:9092 --group group1 --topic payment_requests --reset-offsets --to-offset <CURRENT-OFFSET + 1> --execute
  ```
- Restart the app.

#### Solution 3 - Adjust Retention Period
- Default retention period is 604800000 ms (7 days). Adjust retention period:
  ```bash
  kafka-configs --zookeeper localhost:2181 --entity-type topics --entity-name payment_requests --alter --add-config retention.ms=5
  ```
  OR
  ```bash
  kafka-configs --bootstrap-server localhost:9092 --alter --entity-type topics --entity-name payment_requests --add-config retention.ms=5
  ```

### Run with Application-Antidote Properties
```bash
-Dspring.profiles.active=antidote
```
to see how to gracefully handle poison pills.

For a detailed explanation, visit [here](https://medium.com/p/1eb330e0c703).

### Curl Request for Testing
Run the below on a terminal screen or you can import it in Postman as a request
```bash
curl --location --request POST 'http://localhost:9090/produce' \
--header 'Content-Type: application/json' \
--data-raw '{
"id": 1234,
"type" : "name2",
"amount" : 1.2,
"currency" : "EUR"
}'
```

This guide outlines the setup, handling of poison pill scenarios, and testing steps for your Kafka application. If you have any questions or need further assistance, refer to the linked GitHub repository or reach out to the project contributors.
