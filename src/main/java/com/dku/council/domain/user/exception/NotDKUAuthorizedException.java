package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotDKUAuthorizedException extends LocalizedMessageException {
    public NotDKUAuthorizedException() {
        super(HttpStatus.BAD_REQUEST, "required.dku-authorization");
    }

    public NotDKUAuthorizedException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "required.dku-authorization");
    }
}
