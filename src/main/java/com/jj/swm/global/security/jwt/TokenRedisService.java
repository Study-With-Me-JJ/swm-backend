package com.jj.swm.global.security.jwt;

import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.exception.auth.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenRedisService {

    private final StringRedisTemplate stringRedisTemplate;

    public void saveRefreshToken(String userId, String refreshToken) {
        stringRedisTemplate.opsForValue()
                .set(userId, refreshToken, Duration.ofMillis(ExpirationTime.REFRESH_TOKEN.getValue()));
    }

    public void saveAccessTokenForLogout(String accessToken){
        stringRedisTemplate.opsForValue()
                .set(accessToken, "logout", Duration.ofMillis(ExpirationTime.ACCESS_TOKEN.getValue()));
    }

    public String findByUserIdOrThrow(String userId) {
        String refreshToken = stringRedisTemplate.opsForValue().get(userId);

        if(refreshToken == null)
            throw new TokenException(ErrorCode.UNAUTHORIZED_USER, "Unauthorized User");

        return refreshToken;
    }

    public void deleteByUserId(String userId) {
        stringRedisTemplate.delete(userId);
    }

    public String findByAccessToken(String accessToken) {
        return stringRedisTemplate.opsForValue().get(accessToken);
    }
}
