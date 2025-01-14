package com.jj.swm.global.exception;

import com.jj.swm.global.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlobalException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;
}