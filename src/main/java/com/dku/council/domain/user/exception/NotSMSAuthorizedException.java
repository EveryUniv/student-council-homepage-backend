package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotSMSAuthorizedException extends LocalizedMessageException {
    public NotSMSAuthorizedException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "required.sms-authorization");
    }
}
