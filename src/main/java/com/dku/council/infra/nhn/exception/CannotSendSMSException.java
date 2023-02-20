package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotSendSMSException extends LocalizedMessageException {
    public CannotSendSMSException() {
        super(HttpStatus.BAD_REQUEST, "failed.send-sms");
    }

    public CannotSendSMSException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.send-sms");
    }
}
