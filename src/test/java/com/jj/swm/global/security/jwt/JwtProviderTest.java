package com.jj.swm.global.security.jwt;

import com.jj.swm.domain.user.auth.dto.response.Token;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.exception.auth.TokenException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private JwtProvider jwtProvider;

    @Mock
    private TokenRedisService tokenRedisService;

    private final String secureKey
            = "2qcd0II/w2yz57Lj2/iwsL8zI2sCzjNyZBmmeC6iyEPDfjZY2XgRD9oTYLOi5PEYnfrrh3eqEwyU3o6XbOb3gQ==";

    private SecretKey secretKey;

    @BeforeEach
    void setup() {
        jwtProvider = new JwtProvider(tokenRedisService);

        ReflectionTestUtils.setField(jwtProvider, "key", secureKey);
        ReflectionTestUtils.setField(jwtProvider, "cookieSameSite", "None");
        ReflectionTestUtils.setField(jwtProvider, "cookieSecure", true);

        secretKey = new SecretKeySpec(
                secureKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );

        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
    }

    @Test
    @DisplayName("토큰 생성에 성공한다.")
    void generateTokens_Success() {
        //given
        User user = UserFixture.createUser();
        doNothing().when(tokenRedisService).saveRefreshToken(any(String.class), any(String.class));

        //when
        Token token = jwtProvider.generateTokens(user);

        //then
        assertNotNull(token.accessToken());
        assertNotNull(token.refreshToken());
        assertThat(token.accessToken().startsWith("eyJ")).isTrue();
        assertThat(token.refreshToken().getValue().startsWith("eyJ")).isTrue();
        verify(tokenRedisService, times(1)).saveRefreshToken(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("Access Token 생성 성공 테스트")
    void generateAccessToken_Success() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // when
        String accessToken = jwtProvider.generateAccessToken(authentication);

        // then
        String userId = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject();
        assertEquals("user-id", userId);
    }

    @Test
    @DisplayName("Refresh Token 생성 성공 테스트")
    void generateRefreshToken_Success() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        doNothing().when(tokenRedisService).saveRefreshToken(any(String.class), any(String.class));

        // when
        ResponseCookie refreshTokenCookie = jwtProvider.generateRefreshToken(authentication);

        // then
        assertEquals(JwtProvider.REFRESH_TOKEN_COOKIE_NAME, refreshTokenCookie.getName());
        assertTrue(refreshTokenCookie.getValue().startsWith("eyJ"));
        String userId = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshTokenCookie.getValue())
                .getPayload()
                .getSubject();
        assertEquals("user-id", userId);
        verify(tokenRedisService, times(1)).saveRefreshToken(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("토큰 reissue 성공 테스트")
    void reissue_Success() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ExpirationTime.REFRESH_TOKEN.getValue());

        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("role", "ROLE_USER")
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(JwtProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        given(tokenRedisService.findByUserIdOrThrow("user-id")).willReturn(refreshToken);

        // when
        Token token = jwtProvider.reissue(cookies);

        // then
        assertThat(token.accessToken().startsWith("eyJ")).isTrue();
        assertThat(token.refreshToken().getValue().startsWith("eyJ")).isTrue();
        String userId = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token.accessToken())
                .getPayload()
                .getSubject();
        assertEquals("user-id", userId);
    }

    @Test
    @DisplayName("쿠키가 존재하지 않는다면 리이슈에 실패한다.")
    void reissue_whenEmptyCookie_Fail() {
        // when & then
        assertThrows(TokenException.class, () -> jwtProvider.reissue(null));
    }

    @Test
    @DisplayName("쿠키에 정해진 리프레시 토큰 이름이 없다면 리이슈에 실패한다.")
    void reissue_whenNotValidCookieName_Fail() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ExpirationTime.REFRESH_TOKEN.getValue());

        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("role", "ROLE_USER")
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(JwtProvider.REFRESH_TOKEN_COOKIE_NAME + "not_valid", refreshToken);

        // when & then
        assertThrows(TokenException.class, () -> jwtProvider.reissue(cookies));
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이라면 리이슈에 실패한다.")
    void reissue_whenNotValidRefreshToken_Fail() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 만료된 토큰 생성
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000); // 과거 날짜로 만료된 토큰 생성

        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("role", "ROLE_USER")
                .issuedAt(now)
                .expiration(expiredDate) // 만료된 날짜
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(JwtProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        // when & then
        assertThrows(TokenException.class, () -> jwtProvider.reissue(cookies));
    }

    @Test
    @DisplayName("Access Token에서 사용자 ID 추출 성공 테스트")
    void getUserSubject_Success() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = jwtProvider.generateAccessToken(authentication);

        // when
        String userId = jwtProvider.getUserSubject(accessToken);

        // then
        assertEquals("user-id", userId);
    }

    @Test
    @DisplayName("Authorization에서 액세스 토큰 추출 성공 테스트")
    void resolveAccessToken_Success() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = jwtProvider.generateAccessToken(authentication);

        // when
        String resolvedAccessToken = jwtProvider.resolveAccessToken(JwtProvider.BEARER + accessToken);

        // then
        assertEquals(accessToken, resolvedAccessToken);
    }

    @Test
    @DisplayName("Authorization에서 액세스 토큰 추출 실패 테스트")
    void resolveAccessToken_whenEmptyAuthorization_thenFail() {
        // when
        String resolvedAccessToken = jwtProvider.resolveAccessToken(null);

        // then
        assertNull(resolvedAccessToken);
    }


    @Test
    @DisplayName("토큰 유효성 검사 성공 테스트")
    void validateToken_Success() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = jwtProvider.generateAccessToken(authentication);

        // when & then
        assertDoesNotThrow(() -> jwtProvider.validateToken(accessToken));
    }

    @Test
    @DisplayName("만료된 토큰일 경우 실패한다.")
    void validateToken_whenExpiredToken_thenFail() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000); // 만료된 날짜

        String expiredToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("role", "ROLE_USER")
                .issuedAt(now)
                .expiration(expiredDate) // 만료된 날짜
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        // when & then
        assertThrows(TokenException.class, () -> jwtProvider.validateToken(expiredToken));
    }

    @Test
    @DisplayName("잘못된 JWT 형식일 경우 실패한다.")
    void validateToken_whenMalformedJwt_thenFail() {
        // given
        String malformedToken = "malformed.token.string"; // 잘못된 토큰 형식

        // when & then
        assertThrows(TokenException.class, () -> jwtProvider.validateToken(malformedToken));
    }

    @Test
    @DisplayName("서명이 잘못된 토큰일 경우 실패한다.")
    void parseClaims_whenInvalidSignature_thenFail() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user-id",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ExpirationTime.REFRESH_TOKEN.getValue());

        // 잘못된 서명 키
        String wrongSecretKey = secureKey + "wrong";

        SecretKey wrongKey = new SecretKeySpec(
                wrongSecretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );

        String invalidSignatureToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("role", "ROLE_USER")
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(wrongKey, Jwts.SIG.HS512) // 잘못된 서명 키 사용
                .compact();

        // when & then
        assertThrows(TokenException.class, () -> jwtProvider.validateToken(invalidSignatureToken));
    }


    @Test
    @DisplayName("로그아웃 상태 토큰 예외 테스트")
    void validateLogout_ThrowsException() {
        // given
        String accessToken = "dummy-token";
        when(tokenRedisService.findByAccessToken(accessToken)).thenReturn("logout");

        // when & then
        Assertions.assertThrows(TokenException.class, () -> jwtProvider.validateLogout(accessToken));
    }

}
