package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotGetInputStreamException extends LocalizedMessageException {
    public CannotGetInputStreamException(Throwable e) {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, "invalid.input-stream");
    }
}
