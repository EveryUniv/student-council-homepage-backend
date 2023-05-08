package com.dku.council.domain.ticket.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AfterTicketPeriodException extends LocalizedMessageException {

    public AfterTicketPeriodException() {
        super(HttpStatus.BAD_REQUEST, "invalid.after.ticket-period");
    }
}
