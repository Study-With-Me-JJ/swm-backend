package com.jj.swm.domain.study.common;

import com.jj.swm.global.exception.GlobalException;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.jj.swm.global.common.enums.ErrorCode.NOT_FOUND;
import static com.jj.swm.global.common.enums.ErrorCode.NOT_VALID;

public class EntityModificationValidator {

    public static <T> void validateAllIdsPresent(
            @NotNull List<Long> idsToCheck,
            @NotNull List<T> objects,
            Function<T, Long> idExtractor,
            String errorMessage
    ) {
        int matchCount = (int) objects.stream()
                .filter(object -> idsToCheck.contains(idExtractor.apply(object)))
                .count();

        if (matchCount != idsToCheck.size()) {
            throw new GlobalException(NOT_FOUND, errorMessage);
        }
    }

    public static <T> List<T> getSafeList(List<T> list) {
        return Optional.ofNullable(list).orElse(Collections.emptyList());
    }

    public static void validateSizeLimit(
            int newSize,
            int minLimit,
            int maxLimit,
            String errorMessage
    ) {
        if (minLimit > newSize || newSize > maxLimit) {
            throw new GlobalException(NOT_VALID, errorMessage);
        }
    }

    public static void validateSizeLimit(
            int newSize,
            int maxLimit,
            String errorMessage
    ) {
        if (newSize > maxLimit) {
            throw new GlobalException(NOT_VALID, errorMessage);
        }
    }
}
