package com.jj.swm.global.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    public void setValueWithExpiration(String key, String value, long timeOut) {
        stringRedisTemplate.opsForValue()
                .set(key, value, Duration.ofMillis(timeOut));
    }

    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
