package com.dku.council.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionDto {
    private final boolean success = false;
    private final HttpStatus status;
    private final String code;
    private final String message;

    public ExceptionDto(CustomException e){
        this.status = e.getErrorCode().getStatus();
        this.code = e.getErrorCode().name();
        this.message = e.getErrorCode().getMessage();
    }

    public ExceptionDto(Exception e){
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = e.getClass().getName();
        this.message = e.getMessage();
    }
}