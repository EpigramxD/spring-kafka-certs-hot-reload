package org.epigram.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory(KafkaProperties kafkaProperties,
                                                           SslBundles sslBundles) {

        Map<String, Object> config = kafkaProperties.buildProducerProperties(sslBundles);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> customKafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties,
                                                    SslBundles sslBundles) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(sslBundles), new StringDeserializer(),
                new StringDeserializer());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            @Qualifier("consumerFactory")
            ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory);
        return concurrentKafkaListenerContainerFactory;
    }

//    @Bean
//    ConsumerFactory<String, String> consumerFactory2(KafkaProperties kafkaProperties,
//                                                    SslBundles sslBundles) {
//        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(sslBundles), new StringDeserializer(),
//                new StringDeserializer());
//    }
//
//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory2(
//            @Qualifier("consumerFactory2")
//            ConsumerFactory<String, String> consumerFactory2) {
//        ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory2);
//        return concurrentKafkaListenerContainerFactory;
//    }



}
