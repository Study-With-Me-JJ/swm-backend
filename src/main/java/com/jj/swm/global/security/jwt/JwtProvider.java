package com.jj.swm.global.security.jwt;

import com.jj.swm.domain.auth.dto.Token;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.exception.auth.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String key;
    private SecretKey secretKey;
    private final TokenRedisService tokenRedisService;

    private static final String KEY_ROLE = "role";

    public static final String BEARER = "Bearer ";

    @PostConstruct
    private void setSecretKey(){
        this.secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Token generateTokens(com.jj.swm.domain.user.entity.User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                "",
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().toSpringRole()))
        );

        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);

        return new Token(accessToken, refreshToken);
    }

    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication, ExpirationTime.ACCESS_TOKEN.getValue());
    }

    public String generateRefreshToken(Authentication authentication) {
        String refreshToken = createToken(authentication, ExpirationTime.REFRESH_TOKEN.getValue());
        tokenRedisService.saveRefreshToken(authentication.getName(), refreshToken); // redis에 저장

        return refreshToken;
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

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
    }

    // 3. accessToken 재발급
    public String reissueAccessToken(String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            validateToken(refreshToken);
            String userId = getUserSubject(refreshToken);

            return generateAccessToken(getAuthentication(tokenRedisService.findByUserIdOrThrow(userId)));
        }
        return null;
    }

    public void validateLogout(String accessToken) {
        String isLogout = tokenRedisService.findByAccessToken(accessToken);

        if(isLogout != null && isLogout.equals("logout"))
            throw new TokenException(ErrorCode.INVALID_TOKEN, "User Logout");
    }

    public void validateToken(String token) {
        parseClaims(token);
    }

    public boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public String resolveToken(String authorization) {
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
}
