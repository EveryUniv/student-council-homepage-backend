package com.dku.council.global.error.exception;

import org.springframework.http.HttpStatus;

public class IllegalTypeException extends LocalizedMessageException {
    public IllegalTypeException() {
        super(HttpStatus.NOT_ACCEPTABLE, "invalid.type");
    }
}
