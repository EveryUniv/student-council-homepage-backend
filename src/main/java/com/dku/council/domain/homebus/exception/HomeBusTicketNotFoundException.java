package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class HomeBusTicketNotFoundException extends LocalizedMessageException {
    public HomeBusTicketNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.homebus-ticket");
    }
}