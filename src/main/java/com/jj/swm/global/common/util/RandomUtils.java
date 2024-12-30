package com.jj.swm.global.common.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class RandomUtils {

    private final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateRandomCode() {
        int randomNumber = 100000 + SECURE_RANDOM.nextInt(900000);

        return String.valueOf(randomNumber);
    }
}
