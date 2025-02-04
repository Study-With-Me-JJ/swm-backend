package com.jj.swm.domain.auth.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.auth.dto.request.LoginRequest;
import com.jj.swm.domain.auth.dto.response.Token;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.entity.UserCredential;
import com.jj.swm.domain.user.fixture.UserFixture;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.global.exception.auth.TokenException;
import com.jj.swm.global.security.jwt.JwtProvider;
import com.jj.swm.global.security.jwt.TokenRedisService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest extends IntegrationContainerSupporter {

    // Target Service Bean
    @Autowired private AuthService authService;

    // Related Bean
    @Autowired private JwtProvider jwtProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    // Repository Bean
    @Autowired private UserCredentialRepository userCredentialRepository;
    @Autowired private TokenRedisService tokenRedisService;

    // Repository Bean For Test
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("유저 로그인에 성공한다.")
    void login_Success() {
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@example.com")
                .value(passwordEncoder.encode("1234"))
                .user(user)
                .build();

        userCredentialRepository.save(userCredential);

        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("test@example.com")
                .password("1234")
                .build();

        //when
        Token token = authService.login(loginRequest);

        //then
        String refreshToken = tokenRedisService.findByUserIdOrThrow(user.getId().toString());
        assertThat(token.refreshToken().toString().split(";")[0]).isEqualTo("s_rt=" + refreshToken);
        assertThat(jwtProvider.getUserSubject(token.accessToken())).isEqualTo(user.getId().toString());
    }

    @Test
    @DisplayName("저장되지 않은 유저라면 로그인에 실패한다.")
    void login_whenInValidUser_thenFail() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("test@example.com")
                .password("1234")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> authService.login(loginRequest)
        );
    }

    @Test
    @DisplayName("비밀번호가 매치되지 않는다면 로그인에 실패한다.")
    void login_whenNotMatchPassword_thenFail() {
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@example.com")
                .value(passwordEncoder.encode("1234"))
                .user(user)
                .build();

        userCredentialRepository.save(userCredential);

        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("test@example.com")
                .password("12345")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> authService.login(loginRequest)
        );
    }

    @Test
    @DisplayName("유저 로그아웃에 성공한다.")
    void logout_Success() {
        //given
        User user = UserFixture.createUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                "",
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().toSpringRole()))
        );

        String accessToken = jwtProvider.generateAccessToken(authentication);
        ResponseCookie refreshTokenCookie = jwtProvider.generateRefreshToken(authentication);

        tokenRedisService.saveRefreshToken(user.getId().toString(), refreshTokenCookie.toString().split(";")[0]);

        //when
        authService.logout("Bearer " + accessToken);

        //then
        assertThat(tokenRedisService.findByAccessToken(accessToken)).isEqualTo("logout");
        Assertions.assertThrows(TokenException.class,
                () -> tokenRedisService.findByUserIdOrThrow(user.getId().toString())
        );
    }

    @Test
    @DisplayName("유저 리이슈에 성공한다.")
    void reissue_Success() {
        //given
        User user = UserFixture.createUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                "",
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().toSpringRole()))
        );

        ResponseCookie refreshTokenCookie = jwtProvider.generateRefreshToken(authentication);

        tokenRedisService.saveRefreshToken(user.getId().toString(),
                refreshTokenCookie.toString().substring(5).split(";")[0]);

        Cookie[] cookies = new Cookie[1];
        Cookie servletCookie = new Cookie(refreshTokenCookie.getName(), refreshTokenCookie.getValue());
        servletCookie.setDomain(refreshTokenCookie.getDomain());
        servletCookie.setPath(refreshTokenCookie.getPath());
        servletCookie.setHttpOnly(refreshTokenCookie.isHttpOnly());
        servletCookie.setSecure(refreshTokenCookie.isSecure());
        servletCookie.setMaxAge((int) refreshTokenCookie.getMaxAge().getSeconds());

        cookies[0] = servletCookie;

        //when
        Token token = authService.reissue(cookies);

        //then
        String refreshToken = tokenRedisService.findByUserIdOrThrow(user.getId().toString());
        assertThat(token.refreshToken().toString().split(";")[0]).isEqualTo("s_rt=" + refreshToken);
        assertThat(jwtProvider.getUserSubject(token.accessToken())).isEqualTo(user.getId().toString());
    }

    @Test
    @DisplayName("쿠키 값이 없다면 리이슈에 실패한다.")
    void reissue_whenEmptyCookie_thenFail() {
        //when & then
        Assertions.assertThrows(TokenException.class, () -> authService.reissue(null));
    }
}
