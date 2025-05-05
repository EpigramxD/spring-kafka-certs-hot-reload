package org.epigram.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
//public class StaticKeyAuthenticationFilter extends OncePerRequestFilter {
//    @Value("${authorization.key}")
//    private String authorizationKey;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String requestAuthorizationKey = request.getHeader("Authorization");
//
//        if (requestAuthorizationKey != null && requestAuthorizationKey.equals(authorizationKey)) {
//            filterChain.doFilter(request, response);
//        } else {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        }
//    }
//}
