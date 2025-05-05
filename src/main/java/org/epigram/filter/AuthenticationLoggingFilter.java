package org.epigram.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.Nullable;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Slf4j
//public class AuthenticationLoggingFilter extends OncePerRequestFilter {
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String processId = request.getHeader("Process-ID");
//
//        log.info("Successfully authenticated request with Process-ID: {}", processId);
//        filterChain.doFilter(request, response);
//    }
//}
