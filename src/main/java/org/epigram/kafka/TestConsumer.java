package org.epigram.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TestConsumer {
    private volatile static AtomicInteger messageCounter = new AtomicInteger(0);

    @KafkaListener(topics = "TEST", groupId = "test", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        System.out.println("Consumer: " + consumerRecord.value());
        System.out.println("Message counter: " + count());
    }

    private synchronized int count() {
        return messageCounter.addAndGet(1);
    }
}
