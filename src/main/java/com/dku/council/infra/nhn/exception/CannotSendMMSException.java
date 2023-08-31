package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotSendMMSException extends LocalizedMessageException {
    public CannotSendMMSException() {
        super(HttpStatus.BAD_REQUEST, "failed.send-mms");
    }

    public CannotSendMMSException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.send-mms");
    }
}
