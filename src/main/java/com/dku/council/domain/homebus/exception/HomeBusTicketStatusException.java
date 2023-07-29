package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class HomeBusTicketStatusException extends LocalizedMessageException {
    public HomeBusTicketStatusException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
