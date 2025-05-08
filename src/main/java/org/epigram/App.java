package org.epigram;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

@Slf4j
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
        System.out.println(records.size());
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @SneakyThrows
    @EventListener(ApplicationStartedEvent.class)
    public void addUpdateHandlerToKafkaSslBundle(ApplicationStartedEvent e) {
        addUpdateHandlerToKafkaSslBundle();
    }

    /**
     * Добавление handler'ов на sslbundl'ы, используемые в kafka producer'ах и consumer'ах
     */
    private void addUpdateHandlerToKafkaSslBundle() {
        log.info("Adding update handlers to ssl bundles used for kafka producers and consumers");
        sslBundles.addBundleUpdateHandler("kafka", sslBundle -> {
            consumerFactory.updateConfigs(kafkaProperties.get(0).buildConsumerProperties(sslBundles));
            log.info("Consumer factory config is updated");
            registry.stop();
            registry.start();
            log.info("Consumer containers are restarted");
            kafkaTemplate.getProducerFactory().updateConfigs(kafkaProperties.get(0).buildProducerProperties(sslBundles));
            log.info("Kafka producer factory config is updated");
//            kafkaTemplate.getProducerFactory().closeThreadBoundProducer();
            kafkaTemplate.getProducerFactory().reset();
            log.info("Kafka producer factory was reset");

            System.out.println("Config was reset");
        });
        log.info("Update handlers were successfully added to ssl bundles used for kafka producers and consumers");
    }

    /**
     * Тест на равенство бандла, полученного из sslbundles и из producer factory
     */
    private void testSearchSslBundleOfKafkaTemplate() {
        String bundleName = kafkaProperties.get(0).getProducer().getSsl().getBundle();
        SslBundle bundleFromFactory = (SslBundle) kafkaTemplate.getProducerFactory().getConfigurationProperties()
                .get(SslBundle.class.getName());
        SslBundle bundleFromRegistry = sslBundles.getBundle(bundleName);
        System.out.println(bundleFromFactory.equals(bundleFromRegistry));
    }

    /**
     * Тест на равенство каждой consumer factory полученной container factory из контекста спринга
     * с каждой consumer factory, полученной из контейнера
     */
    private void testSearchContainerByContainerFactory() {

        var containers = registry.getAllListenerContainers().stream().toList();
        containers.forEach(container -> {
            kafkaListenerContainerFactories.forEach(concurrentListenerContainerFactory -> {
                Field consumerFactoryFromContainerField = Arrays.stream(container.getClass().getSuperclass().getDeclaredFields())
                        .filter(field -> field.getType() == ConsumerFactory.class).toList().get(0);
                consumerFactoryFromContainerField.setAccessible(true);
                try {
                    ConsumerFactory<?, ?> consumerFactoryFromContainer = (ConsumerFactory<?, ?>) consumerFactoryFromContainerField.get(container);
                    log.info("Consumer factory of container factory from application context and consumer factory of container equality: "
                            + consumerFactoryFromContainer.equals(concurrentListenerContainerFactory.getConsumerFactory()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    consumerFactoryFromContainerField.setAccessible(false);
                }
            });
        });
    }

    // scheduler для отправки в топик TEST сообщений из очереди org.epigram.App.records
    @Scheduled(fixedRate = 50)
    public void sendMessagesToTestTopic() {
        try {
            var record = records.poll();
            if (record != null) {
                System.out.println("Sending message: start");
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("TEST", "TEST");
                kafkaTemplate.send(producerRecord);
                System.out.println("Sending message: end");
            }
            else {
                System.out.println("no records left");
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Добавление хендлера sslbundle для обновления фабрики запросов в RestTemplate
     */
//    @EventListener(ApplicationReadyEvent.class)
//    public void addHandlerToRestTemplateSslBundle(ApplicationReadyEvent e) {
//        sslBundles.addBundleUpdateHandler("client", sslBundle -> {
//            try {
//                System.out.println("Updating request factory");
//                PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
//                        .useSystemProperties()
//                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
//                                .useSystemProperties()
//                                .setSslContext(SSLContextBuilder.create()
//                                        .setProtocol(sslBundle.getProtocol())
//                                        .loadTrustMaterial(sslBundle.getStores().getTrustStore(), null)
//                                        .loadKeyMaterial(sslBundle.getStores().getKeyStore(), null)
//                                        .build())
//                                .build())
//                        .setDefaultConnectionConfig(ConnectionConfig.custom()
//                                .setConnectTimeout(Timeout.ofMinutes(1))
//                                .build())
//                        .setDefaultSocketConfig(SocketConfig.custom()
//                                .setSoTimeout(Timeout.ofMinutes(1))
//                                .build())
//                        .setMaxConnTotal(100)
//                        .setMaxConnPerRoute(100)
//                        .build();
//
//                ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
//                        HttpClients.custom()
//                                .useSystemProperties()
//                                .setConnectionManager(connectionManager)
//                                .setDefaultRequestConfig(RequestConfig.custom()
//                                        .setConnectionRequestTimeout(Timeout.ofMinutes(1))
//                                        .build())
//                                .build());
//
//                restTemplate.setRequestFactory(requestFactory);
//                System.out.println("Request factory was updated");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }


    /**
     * Отправка запросов каждые 2 минуты с помощью RestTemplate на /hello
     */
//    @Scheduled(fixedRate = 120000)
//    public void sendHelloRequest() {
//        System.out.println("Sending request");
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/hello", String.class);
//        System.out.println(responseEntity.getBody());
//    }

}