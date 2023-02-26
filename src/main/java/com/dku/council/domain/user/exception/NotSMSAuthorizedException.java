package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotSMSAuthorizedException extends LocalizedMessageException {
    public NotSMSAuthorizedException() {
        super(HttpStatus.FORBIDDEN, "required.sms-authorization");
    }
}
