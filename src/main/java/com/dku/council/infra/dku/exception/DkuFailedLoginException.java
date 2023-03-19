package com.dku.council.infra.dku.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class DkuFailedLoginException extends LocalizedMessageException {

    public DkuFailedLoginException() {
        super(HttpStatus.BAD_REQUEST, "failed.dku-login");
    }

    public DkuFailedLoginException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.dku-login");
    }

    public DkuFailedLoginException(String customMessage) {
        super(HttpStatus.BAD_REQUEST, customMessage);
        setCustomMessage(customMessage);
    }
}
