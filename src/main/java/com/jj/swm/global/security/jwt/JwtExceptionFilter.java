package com.jj.swm.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.exception.auth.AuthException;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.global.exception.auth.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenException | AuthException e) {
            setExceptionResponse(response, e);
        }
    }

    private void setExceptionResponse(HttpServletResponse response, GlobalException e) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(e)));
    }
}
