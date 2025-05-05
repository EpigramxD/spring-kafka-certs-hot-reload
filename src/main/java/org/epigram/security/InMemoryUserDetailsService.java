package org.epigram.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

//public class InMemoryUserDetailsService implements UserDetailsService {
//    private final Map<String, UserDetails> userDetails;
//
//    public InMemoryUserDetailsService(Map<String, UserDetails> userDetails) {
//        this.userDetails = userDetails;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserDetails details = userDetails.get(username);
//        if (details == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//        return details;
//    }
//}
