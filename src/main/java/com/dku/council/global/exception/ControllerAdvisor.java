package com.dku.council.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ExceptionDto> exceptionHandler(CustomException e) {
        return ResponseEntity.status(e.errorCode.getStatus()).body(new ExceptionDto(e));
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ExceptionDto> exception(Exception e) {
        return ResponseEntity.internalServerError().body(new ExceptionDto(e));
    }
}
