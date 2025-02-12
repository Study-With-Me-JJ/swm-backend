package com.jj.swm.global.security.jwt;

import com.jj.swm.domain.user.auth.dto.response.Token;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.exception.auth.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    private SecretKey secretKey;
    private final TokenRedisService tokenRedisService;

    private static final String KEY_ROLE = "role";
    public static final String BEARER = "Bearer ";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "s_rt";

    @PostConstruct
    private void setSecretKey(){
        this.secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Token generateTokens(com.jj.swm.domain.user.core.entity.User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                "",
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().toSpringRole()))
        );

        String accessToken = generateAccessToken(authentication);
        ResponseCookie refreshTokenCookie = generateRefreshToken(authentication);

        return new Token(accessToken, refreshTokenCookie);
    }

    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication, ExpirationTime.ACCESS_TOKEN.getValue());
    }

    public ResponseCookie generateRefreshToken(Authentication authentication) {
        String refreshToken = createToken(authentication, ExpirationTime.REFRESH_TOKEN.getValue());
        tokenRedisService.saveRefreshToken(authentication.getName(), refreshToken); // redis에 저장

        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(ExpirationTime.REFRESH_TOKEN.getValue())
                .sameSite(cookieSameSite)
                .build();
    }

    public Token reissue(Cookie[] cookies) {
        String refreshToken = validateRefreshTokenAboutCookies(cookies);
        String userId = getUserSubject(refreshToken);

        Authentication authentication = getAuthentication(tokenRedisService.findByUserIdOrThrow(userId));
        String accessToken = generateAccessToken(authentication);
        ResponseCookie refreshTokenCookie = generateRefreshToken(authentication);

        return new Token(accessToken, refreshTokenCookie);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public void validateLogout(String accessToken) {
        String isLogout = tokenRedisService.findByAccessToken(accessToken);

        if(isLogout != null && isLogout.equals("logout"))
            throw new TokenException(ErrorCode.INVALID_TOKEN, "User Logout");
    }

    public void validateToken(String token) {
        parseClaims(token);
    }

    public String resolveAccessToken(String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER)) {
            return authorization.substring(BEARER.length());
        }
        return null;
    }

    public String getUserSubject(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private String createToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(KEY_ROLE, authorities)
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    private String validateRefreshTokenAboutCookies(Cookie[] cookies) {
        if(cookies == null){
            throw new TokenException(ErrorCode.INVALID_TOKEN, "No Refresh Token found in cookies");
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> JwtProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenException(ErrorCode.INVALID_TOKEN, "No Refresh Token found in cookies"));

        parseClaims(refreshToken);

        return refreshToken;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorCode.EXPIRED_TOKEN, "Expired Token");
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorCode.INVALID_TOKEN, "Invalid Token");
        } catch (SignatureException e) {
            throw new TokenException(ErrorCode.INVALID_JWT_SIGNATURE, "Invalid JWT Signature");
        }
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
    }
}
