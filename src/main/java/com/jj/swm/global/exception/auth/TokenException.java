package com.jj.swm.global.exception.auth;

import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;

public class TokenException extends GlobalException {
    public TokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
