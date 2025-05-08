package org.epigram.kafka;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.ArrayList;
import java.util.List;

public class TrackingConcurrentKafkaListenerContainerFactory<K, V> extends ConcurrentKafkaListenerContainerFactory<K, V> {
    private final List<MessageListenerContainer> createdContainers = new ArrayList<>();

    @Override
    protected ConcurrentMessageListenerContainer<K, V> createContainerInstance(KafkaListenerEndpoint endpoint) {
        ConcurrentMessageListenerContainer<K, V> container = super.createContainerInstance(endpoint);
        createdContainers.add(container);
        return container;
    }

    public List<MessageListenerContainer> getCreatedContainers() {
        return this.createdContainers;
    }
}
