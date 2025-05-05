package org.epigram.controller;

import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    SslBundles sslBundles;

    @GetMapping
    @SneakyThrows
    public String hello() {
//        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("TEST", "TEST");
//        kafkaTemplate.send(producerRecord);
        Thread.sleep(60000);
        return lel(false);
    }

    private String lel(boolean isGreeting) {
        if (isGreeting) {
            return "xd";
        }
        return "Hello";
    }
}
