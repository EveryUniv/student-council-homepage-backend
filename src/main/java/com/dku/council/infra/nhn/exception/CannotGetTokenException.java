package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotGetTokenException extends LocalizedMessageException {
    public CannotGetTokenException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "failed.get-token");
    }

    public CannotGetTokenException(Throwable t) {
        super(t, HttpStatus.INTERNAL_SERVER_ERROR, "failed.get-token");
    }
}
