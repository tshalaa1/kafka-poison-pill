### Start Zookeeper 
```bash
zookeeper-server-start /usr/local/etc/zookeeper/zoo.cfg
```

### Start Kafka Broker
```bash
kafka-server-start /usr/local/etc/kafka/server.properties
```

### Start Schema Registry
```bash
docker run -p 8081:8081 -e SCHEMA_REGISTRY_HOST_NAME=schema-registry \
-e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS="{localhost}:9092" \
-e SCHEMA_REGISTRY_KAFKASTORE_SASL_MECHANISM=PLAIN \
confluentinc/cp-schema-registry:latest
```

### Create Kafka Topic
```bash
kafka-topics --create --topic payment_requests --bootstrap-server localhost:9092
```

### Setup Avro Schema
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

### Run Default with Application Properties
```bash
mvn spring-boot:run
```

To see poison pill logs:
- Introduce poison pill!
  ```bash
  kafka-console-producer --broker-list localhost:9092 --topic payment_requests
  ```
  
- In the resulting terminal screen inserting any data will cause an issue because it does not follow the Avro Schema
  
  ```bash
  {"id": 1234, "type": "name", "amount": 4.7}
  ```

### Solutions to Handle Poison Pill (without antidote)

#### Solution 1 - Delete Topic and Recreate
- Delete the topic and recreate:
  ```bash
  kafka-topics --bootstrap-server localhost:9092 --delete --topic payment_requests
  ```
- Restart the application for the solution to take effect.

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

### Run with Application-Antidote Properties
To see how the app can gracefully handle poison pills, run the application with the application-antidote.properties
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=antidote
```

Now introducing any erroneous message through the producer in CLI will just result in logging the problem once.

For further details and explanations, visit [the medium article](https://medium.com/p/1eb330e0c703).

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