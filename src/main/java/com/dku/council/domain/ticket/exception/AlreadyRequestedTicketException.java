package com.dku.council.domain.ticket.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyRequestedTicketException extends LocalizedMessageException {

    public AlreadyRequestedTicketException() {
        super(HttpStatus.BAD_REQUEST, "already.issued-ticket");
    }
}
