package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotJukjeonException extends LocalizedMessageException {
    public NotJukjeonException() {
        super(HttpStatus.BAD_REQUEST, "invalid.only-jukjeon");
    }
}