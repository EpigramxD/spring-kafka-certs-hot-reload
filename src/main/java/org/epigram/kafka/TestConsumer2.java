package org.epigram.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestConsumer2 {
//    @KafkaListener(topics = "TEST", groupId = "test", containerFactory = "kafkaListenerContainerFactory2")
//    public void consume(ConsumerRecord<String, String> consumerRecord) {
//        System.out.println("Consumer: " + consumerRecord.value());
//    }
}
