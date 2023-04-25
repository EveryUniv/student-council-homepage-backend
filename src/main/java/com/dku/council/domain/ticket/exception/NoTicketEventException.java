package com.dku.council.domain.ticket.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NoTicketEventException extends LocalizedMessageException {

    public NoTicketEventException() {
        super(HttpStatus.NOT_FOUND, "notfound.ticket-event");
    }
}
