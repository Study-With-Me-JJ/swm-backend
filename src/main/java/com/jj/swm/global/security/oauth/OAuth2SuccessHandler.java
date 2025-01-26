package com.jj.swm.global.security.oauth;

import com.jj.swm.domain.auth.dto.response.Token;
import com.jj.swm.global.security.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private static final String redirectUrl = "http://localhost:3000/login";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        String accessToken = jwtProvider.generateAccessToken(authentication);
        ResponseCookie refreshTokenCookie = jwtProvider.generateRefreshToken(authentication);

        response.addHeader(AUTHORIZATION, JwtProvider.BEARER + accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.sendRedirect(redirectUrl);
    }
}
