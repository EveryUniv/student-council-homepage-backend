package com.dku.council.global.error.exception;

import org.springframework.http.HttpStatus;

public class MajorNotFoundException extends LocalizedMessageException {
    public MajorNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "notfound.major");
    }
}
