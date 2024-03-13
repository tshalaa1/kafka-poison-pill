package com.tshalaa1;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final ConsumerFactory<String, FoodRecommendation> consumerFactory;

    @Autowired
    public KafkaConsumer(ConsumerFactory<String, FoodRecommendation> consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

//    @KafkaListener(topics = "food_recommendations", groupId = "group1")
//    public void consumeBasicSetup(FoodRecommendation foodRecommendation) {
//        System.out.println("Received message (Basic Setup): " + foodRecommendation);
//        // Basic setup to process messages
//    }

    @KafkaListener(topics = "food_recommendations", groupId = "group1")
    public void consumeWithAntidotes(ConsumerRecord<String, FoodRecommendation> record) {
        System.out.println("Received message (Antidotes): " + record.value());
        // Implement logic to handle poison pill messages gracefully
        // For example, log the error and continue processing other messages
    }
}
