package com.jj.swm.global.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jj.swm.global.exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public record ApiResponse<T>(
        @JsonIgnore
        HttpStatus httpStatus,
        String message,
        @Nullable T data
) {

    public static <T> ApiResponse<T> ok(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.OK, "정상 처리 되었습니다.", data);
    }

    public static <T> ApiResponse<T> created(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.CREATED, "정상적으로 생성되었습니다.", data);
    }

    public static <T> ApiResponse<T> fail(final GlobalException e) {
        return new ApiResponse<>(e.getErrorCode().getHttpStatus(), e.getMessage(), null);
    }
}