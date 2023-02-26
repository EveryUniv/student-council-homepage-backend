package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotInitializedException extends LocalizedMessageException {
    public NotInitializedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected");
    }
}
