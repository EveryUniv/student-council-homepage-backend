package com.dku.council.global.error.exception;

import org.springframework.http.HttpStatus;

public class NotSupportedMethodException extends LocalizedMessageException {
    public NotSupportedMethodException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "notsupport.http-method");
    }
}
