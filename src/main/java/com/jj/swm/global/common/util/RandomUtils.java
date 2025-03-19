package com.jj.swm.global.common.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class RandomUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String NUMBERS = "0123456789012345678901234567890123456789012345678901234567890";

    public String generateRandomCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(NUMBERS.charAt(SECURE_RANDOM.nextInt(NUMBERS.length())));
        }
        return builder.toString();
    }
}
