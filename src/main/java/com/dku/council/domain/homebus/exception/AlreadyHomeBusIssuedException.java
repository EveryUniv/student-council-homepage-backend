package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyHomeBusIssuedException extends LocalizedMessageException {
    public AlreadyHomeBusIssuedException() {
        super(HttpStatus.BAD_REQUEST, "already.homebus-issued");
    }
}
