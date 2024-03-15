package com.tshalaa1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String consumerBootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String producerBootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String producerKeySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String producerValueSerializer;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String consumerKeyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String consumerValueDeserializer;

    @Value("${schema.registry.url}")
    private String schemaRegistryUrl; // Add this line for Schema Registry URL


    @Bean
    public ConsumerFactory<String, FoodRecommendation> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("spring.kafka.consumer.properties.schema.registry.url", schemaRegistryUrl);

        // Set the ErrorHandlingDeserializer for key and value deserialization
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Set the deserializer configs for the ErrorHandlingDeserializer
        // spring.deserializer.key.delegate.clas
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, "org.apache.kafka.common.serialization.StringDeserializer");


        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, FoodRecommendation> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, FoodRecommendation> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerKeySerializer);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerValueSerializer);
        props.put("schema.registry.url", schemaRegistryUrl);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConsumerRecordRecoverer consumerRecordRecoverer(KafkaTemplate<String, FoodRecommendation> kafkaTemplate) {
        return new ErrorMessageRecoverer();
    }


    /*
    The ConcurrentKafkaListenerContainerFactory is needed to create Kafka listener containers specifically tailored for
    concurrent message consumption. While the ConsumerFactory provides the configuration for creating Kafka consumers,
    the ConcurrentKafkaListenerContainerFactory builds on top of it by allowing for concurrent message processing within
     each container. This is useful for achieving higher throughput and scalability in message processing, especially in
      scenarios where multiple messages need to be processed concurrently. Additionally, it allows for configuring error
       handling mechanisms, such as retry policies or error recovery strategies, specific to message consumption within
       the listener containers.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FoodRecommendation> kafkaListenerContainerFactory(ConsumerFactory<String, FoodRecommendation> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, FoodRecommendation> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(consumerRecordRecoverer(kafkaTemplate())));
        return factory;
    }
}
