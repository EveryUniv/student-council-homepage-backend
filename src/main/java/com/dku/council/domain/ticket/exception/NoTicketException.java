package com.dku.council.domain.ticket.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NoTicketException extends LocalizedMessageException {

    public NoTicketException() {
        super(HttpStatus.NOT_FOUND, "notfound.ticket");
    }
}
