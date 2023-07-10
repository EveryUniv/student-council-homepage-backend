package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class HomeBusNotFoundException extends LocalizedMessageException {
    public HomeBusNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.homebus");
    }
}