package com.jj.swm.global.exception.handler;

import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class SWMExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    protected ApiResponse<Void> handleGlobalException(GlobalException e){
        log.error("GlobalException: {}", e.getErrorCode().getMessage());
        return ApiResponse.fail(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        return ApiResponse.fail(ErrorCode.NOT_VALID, "Not Valid Argument");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());
        return ApiResponse.fail(ErrorCode.NOT_VALID, "JSON Format Error");
    }

    @ExceptionHandler(IOException.class)
    public ApiResponse<Void> handleIOException(IOException e) {
        log.error("IOException: {}", e.getMessage());
        return ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "IO Error");
    }
}
