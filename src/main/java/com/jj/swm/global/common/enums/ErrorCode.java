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
    UNAUTHORIZED_USER(10003, HttpStatus.UNAUTHORIZED, "Unauthorized User"),
    INVALID_TOKEN(10004, HttpStatus.UNAUTHORIZED, "Invalid Token"),
    INVALID_JWT_SIGNATURE(10005, HttpStatus.UNAUTHORIZED, "Invalid JWT Signature"),
    EXPIRED_TOKEN(10006, HttpStatus.UNAUTHORIZED, "Expired Token"),
    INTERNAL_SERVER_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
