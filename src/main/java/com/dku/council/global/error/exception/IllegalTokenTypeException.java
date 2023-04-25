package com.dku.council.global.error.exception;

import org.springframework.http.HttpStatus;

public class IllegalTokenTypeException extends LocalizedMessageException {

    public IllegalTokenTypeException() {
        super(HttpStatus.NOT_ACCEPTABLE, "invalid.token-type");
    }
}
