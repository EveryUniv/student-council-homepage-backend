package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class FullSeatsException extends LocalizedMessageException {
    public FullSeatsException(int limit) {
        super(HttpStatus.BAD_REQUEST, "invalid.full-seats", limit);
    }
}
