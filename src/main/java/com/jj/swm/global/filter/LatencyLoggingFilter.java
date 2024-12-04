package com.jj.swm.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LatencyLoggingFilter extends OncePerRequestFilter {

    private final LatencyRecorder latencyRecorder;
    private final QueryInspector queryInspector;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        latencyRecorder.start();

        filterChain.doFilter(request, response);

        double latencyForSeconds = latencyRecorder.getLatencyForSeconds();
        int queryCount = queryInspector.getQueryCount();
        String requestURI = request.getRequestURI();

        if(!PatternMatchUtils.simpleMatch("/admin/health", requestURI)) {
            log.info("Latency : {}s, Query count : {}, Request URI : {}", latencyForSeconds, queryCount, requestURI);
        }

        MDC.clear();
    }
}
