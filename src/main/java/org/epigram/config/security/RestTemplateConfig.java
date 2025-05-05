package org.epigram.config.security;

import lombok.SneakyThrows;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
                                     SslBundles sslBundles) {
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClients.custom()
                        .useSystemProperties()
                        .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                                .useSystemProperties()
                                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                        .useSystemProperties()
                                        .setSslContext(createSSLContext(sslBundles))
                                        .build())
                                .setDefaultConnectionConfig(ConnectionConfig.custom()
                                        .setConnectTimeout(Timeout.ofMinutes(3))
                                        .build())
                                .setDefaultSocketConfig(SocketConfig.custom()
                                        .setSoTimeout(Timeout.ofMinutes(3))
                                        .build())
                                .setMaxConnTotal(100)
                                .setMaxConnPerRoute(100)
                                .build())
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectionRequestTimeout(Timeout.ofMinutes(3))
                                .build())
                        .build()
        );
        return restTemplateBuilder.rootUri("https://localhost:8080")
                .requestFactory(() -> requestFactory)
                .build();
    }

    @SneakyThrows
    private SSLContext createSSLContext(SslBundles sslBundles) {
        SslBundle sslBundle = sslBundles.getBundle("client");
        return SSLContextBuilder.create()
                .setProtocol(sslBundle.getProtocol())
                .loadTrustMaterial(sslBundle.getStores().getTrustStore(), null)
                .loadKeyMaterial(sslBundle.getStores().getKeyStore(), null)
                .build();
        // also works
//        return sslBundles.getBundle("client").createSslContext();
    }
}
