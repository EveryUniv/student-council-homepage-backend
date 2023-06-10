package com.dku.council.global.error.exception;

import org.springframework.http.HttpStatus;

public class OutOfRangeException extends LocalizedMessageException {
    public OutOfRangeException(String range) {
        super(HttpStatus.BAD_REQUEST, "invalid.out-of-range", range);
    }
}
