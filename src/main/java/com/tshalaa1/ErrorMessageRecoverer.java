package com.tshalaa1;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Component
public class ErrorMessageRecoverer implements ConsumerRecordRecoverer {
    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        System.out.println("we have reached the ConsumerRecordRecoverer for the message: " + consumerRecord.value());
    }
}
