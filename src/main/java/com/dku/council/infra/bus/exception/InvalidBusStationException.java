package com.dku.council.infra.bus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidBusStationException extends LocalizedMessageException {
    public InvalidBusStationException() {
        super(HttpStatus.BAD_REQUEST, "invalid.bus-station");
    }
}