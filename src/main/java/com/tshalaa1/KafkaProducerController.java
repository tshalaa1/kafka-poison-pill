package com.tshalaa1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class KafkaProducerController {

    private final KafkaTemplate<String, FoodRecommendation> kafkaTemplate;
    private static final String TOPIC = "food_recommendations";

    @Autowired
    public KafkaProducerController(KafkaTemplate<String, FoodRecommendation> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping(value = "/produce")
    public String produceMessage(@RequestBody FoodRecommendation foodRecommendation) {
        kafkaTemplate.send(TOPIC, foodRecommendation);
        return "Message sent to Kafka topic successfully";
    }
}

