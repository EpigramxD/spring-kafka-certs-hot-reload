package org.epigram;

import lombok.SneakyThrows;
import org.apache.commons.lang3.IntegerRange;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.epigram.controller.HelloController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.IntStream;

@EnableScheduling
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class })
public class App {

    private static Queue<ProducerRecord<String, String>> records = new ArrayDeque<>();

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    List<KafkaProperties> kafkaProperties;

    @Autowired
    DataSource dataSource;

    @Autowired
    SslBundles sslBundles;

    @Autowired
    ProducerFactory<String, String> producerFactory;

    @Autowired
    ConsumerFactory<String, String> consumerFactory;

    @Autowired
    List<ConcurrentKafkaListenerContainerFactory<String, String>> kafkaListenerContainerFactories;

    @Autowired
    KafkaListenerEndpointRegistry registry;

    @Autowired
    KafkaListenerAnnotationBeanPostProcessor<?, ?> bpp;

    @Autowired
    RestTemplate restTemplate;

    static {
        IntStream.range(0, 3000).boxed().forEach(i -> records.add(new ProducerRecord<>("TEST", "TEST"+i)));
        System.out.println("asdasdasdasdasd");
        System.out.println(records.size());
        System.out.println("asdasdasdasdasd");
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void xd(ApplicationReadyEvent e) {
        sslBundles.addBundleUpdateHandler("client", sslBundle -> {
            try {
                System.out.println("Updating request factory");
                ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                        HttpClients.custom()
                                .useSystemProperties()
                                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                                        .useSystemProperties()
                                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                                .useSystemProperties()
                                                .setSslContext(SSLContextBuilder.create()
                                                        .setProtocol(sslBundle.getProtocol())
                                                        .loadTrustMaterial(sslBundle.getStores().getTrustStore(), null)
                                                        .loadKeyMaterial(sslBundle.getStores().getKeyStore(), null)
                                                        .build())
                                                .build())
                                        .setDefaultConnectionConfig(ConnectionConfig.custom()
                                                .setConnectTimeout(Timeout.ofMinutes(1))
                                                .build())
                                        .setDefaultSocketConfig(SocketConfig.custom()
                                                .setSoTimeout(Timeout.ofMinutes(1))
                                                .build())
                                        .setMaxConnTotal(100)
                                        .setMaxConnPerRoute(100)
                                        .build())
                                .setDefaultRequestConfig(RequestConfig.custom()
                                        .setConnectionRequestTimeout(Timeout.ofMinutes(1))
                                        .build())
                                .build());
                restTemplate.setRequestFactory(requestFactory);
                System.out.println("Request factory was updated");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    @Scheduled(fixedRate = 120000)
    public void testRest() {
        System.out.println("Sending request");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/hello", String.class);
        System.out.println(responseEntity.getBody());
    }


//
//    @SneakyThrows
//    @EventListener(ApplicationStartedEvent.class)
//    public void xd1(ApplicationStartedEvent e) {
//        System.out.println("xdddddddddddddddddddddd");
//        sslBundles.addBundleUpdateHandler("kafka", sslBundle -> {
////            consumerFactory.updateConfigs(kafkaProperties.get(0).buildConsumerProperties(sslBundles));
////            registry.stop();
////            registry.start();
//            kafkaProperties.get(0).buildProducerProperties(sslBundles);
//            kafkaTemplate.getProducerFactory().updateConfigs(kafkaProperties.get(0).buildProducerProperties(sslBundles));
//            System.out.println("Config is updated");
////            kafkaTemplate.getProducerFactory().closeThreadBoundProducer();
//            kafkaTemplate.getProducerFactory().reset();
//            System.out.println("Config was reset");
//        });
//        System.out.println("xdddddddddddddddddddddd");
//
//        String bundleName = kafkaProperties.get(0).getProducer().getSsl().getBundle();
//        SslBundle bundleFromFactory = (SslBundle) kafkaTemplate.getProducerFactory().getConfigurationProperties()
//                .get(SslBundle.class.getName());
//        SslBundle bundleFromRegistry = sslBundles.getBundle(bundleName);
//        System.out.println(bundleFromFactory.equals(bundleFromRegistry));
//
//
//        var containers = registry.getAllListenerContainers().stream().toList();
//        System.out.println("fact");
//        containers.forEach(container -> {
//            kafkaListenerContainerFactories.forEach(fact -> {
//                var sdf = Arrays.stream(container.getClass().getSuperclass().getDeclaredFields()).filter(field -> field.getType() == ConsumerFactory.class).toList().get(0);
//                sdf.setAccessible(true);
//                try {
//                    ConsumerFactory<?, ?> consumerFactory1 = (ConsumerFactory<?, ?>) sdf.get(container);
//                    System.out.println(consumerFactory1.equals(fact.getConsumerFactory()));
//                } catch (Exception exx) {
//                    exx.getMessage();
//                }
//
//            });
//
//        });
//
//
//    }
//
////    @Scheduled(fixedRate = 50)
//    public void xd() {
//        try {
//            var record = records.poll();
//            if (record != null) {
//                System.out.println("Sending message: start");
//                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("TEST", "TEST");
//                kafkaTemplate.send(producerRecord);
//                System.out.println("Sending message: end");
//            }
//            else {
////                System.out.println("no records left");
//            }
//
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void xd(ApplicationReadyEvent e) {
//        Thread thread = new Thread(() -> {
//            try {
//                while(true) {
//                    System.out.println("Sending message: start");
//                    Thread.sleep(1000);
//                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>("TEST", "TEST");
//                    kafkaTemplate.send(producerRecord);
//                    System.out.println("Sending message: end");
//                }
//            } catch (Exception ex) {
//                System.out.println(ex.getMessage());
//            }
//        });
////        thread.start();
//
//
////        System.out.println(passwordEncoder.encode("12345"));
////        System.out.println(dataSource.getClass().getName());
//
//    }
}