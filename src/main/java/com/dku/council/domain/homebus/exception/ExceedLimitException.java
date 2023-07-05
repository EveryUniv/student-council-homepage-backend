package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class ExceedLimitException extends LocalizedMessageException {
    public ExceedLimitException(String limit) {
        super(HttpStatus.BAD_REQUEST, "invalid.homebus-limit", limit);

    }
}
