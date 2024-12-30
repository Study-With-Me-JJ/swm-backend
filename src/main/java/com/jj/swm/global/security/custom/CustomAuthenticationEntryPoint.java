package com.jj.swm.global.security.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.auth.TokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("인증되지 않은 사용자 접근, requestURI: {}, accessToken: {}",
                request.getRequestURI(), request.getHeader(AUTHORIZATION));
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.fail(ErrorCode.UNAUTHORIZED_USER, "인증되지 않은 사용자의 접근")
                )
        );
    }
}
