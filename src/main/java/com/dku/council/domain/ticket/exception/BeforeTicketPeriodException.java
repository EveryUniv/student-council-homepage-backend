package com.dku.council.domain.ticket.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class BeforeTicketPeriodException extends LocalizedMessageException {

    public BeforeTicketPeriodException() {
        super(HttpStatus.BAD_REQUEST, "invalid.before.ticket-period");
    }
}
