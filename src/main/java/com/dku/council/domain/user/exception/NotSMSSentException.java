package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotSMSSentException extends LocalizedMessageException {
    public NotSMSSentException() {
        super(HttpStatus.BAD_REQUEST, "required.sms-sending");
    }
}
