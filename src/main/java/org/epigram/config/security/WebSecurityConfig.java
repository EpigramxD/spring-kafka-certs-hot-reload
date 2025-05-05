package org.epigram.config.security;

//import org.epigram.filter.StaticKeyAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
       // authentication configuration
//       httpSecurity.httpBasic(Customizer.withDefaults());
       // authorization rules config
        httpSecurity.authorizeHttpRequests(customzier -> customzier.requestMatchers("/hello").permitAll());
//       httpSecurity.authorizeHttpRequests(customzier -> customzier.anyRequest().permitAll());
//       httpSecurity.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class);
//       httpSecurity.addFilterAfter(new AuthenticationLoggingFilter(), BasicAuthenticationFilter.class);
//       httpSecurity.addFilterAt(staticKeyAuthenticationFilter, BasicAuthenticationFilter.class);
       return httpSecurity.build();
    }



}
