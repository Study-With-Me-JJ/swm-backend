package com.jj.swm.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND(10000, HttpStatus.NOT_FOUND, "Not Found"),
    FORBIDDEN(10001, HttpStatus.FORBIDDEN, "Forbidden"),
    NOT_VALID(10002, HttpStatus.BAD_REQUEST, "Not Valid"),

    INTERNAL_SERVER_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
