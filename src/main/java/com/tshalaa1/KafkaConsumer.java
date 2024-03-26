package com.tshalaa1;
import com.tshalaa1.avro.model.PaymentRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {

    private final ConsumerFactory<String, PaymentRequest> consumerFactory;

    @Autowired
    public KafkaConsumer(ConsumerFactory<String, PaymentRequest> consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

    @KafkaListener(topics = "payment_requests", groupId = "group1")
    public void consumer(ConsumerRecord<String, PaymentRequest> record) {
        System.out.println("Received message: " + record.value());
        // Implement logic to process the consumed message
    }
}
