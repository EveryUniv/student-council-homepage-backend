package com.dku.council.domain.ticket.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidTicketPeriodException extends LocalizedMessageException {

    public InvalidTicketPeriodException() {
        super(HttpStatus.BAD_REQUEST, "invalid.ticket-enroll");
    }
}
