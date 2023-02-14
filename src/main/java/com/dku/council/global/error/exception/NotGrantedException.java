package com.dku.council.global.error.exception;

import org.springframework.http.HttpStatus;

public class NotGrantedException extends LocalizedMessageException {
    public NotGrantedException() {
        super(HttpStatus.FORBIDDEN, "required.granted");
    }
}
