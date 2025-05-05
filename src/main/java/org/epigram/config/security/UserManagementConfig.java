package org.epigram.config.security;

import java.security.SecureRandom;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

//@Configuration
//public class UserManagementConfig {
//    @Bean
//    UserDetailsService userDetailsService(DataSource dataSource) {
//        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
//        userDetailsManager.setUsersByUsernameQuery("select username, password, enabled from users where username = ?");
//        userDetailsManager.setAuthoritiesByUsernameQuery("select username, authority, enabled from authorities where username = ?");
//        return new JdbcUserDetailsManager(dataSource);
//    }
//
//    @Bean
//    PasswordEncoder passwordEncoder() throws Exception {
//        return new BCryptPasswordEncoder(10, SecureRandom.getInstanceStrong());
//    }
//}
