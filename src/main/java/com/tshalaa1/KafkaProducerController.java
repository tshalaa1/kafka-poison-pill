package com.tshalaa1;

import com.tshalaa1.avro.model.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class KafkaProducerController {

    private final KafkaTemplate<String, PaymentRequest> kafkaTemplate;
    private static final String TOPIC = "payment_requests";

    @Autowired
    public KafkaProducerController(KafkaTemplate<String, PaymentRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping(value = "/produce")
    public String produceMessage(@RequestBody PaymentRequest paymentRequest) {
        kafkaTemplate.send(TOPIC, paymentRequest);
        return "Message sent to Kafka topic successfully";
    }
}

