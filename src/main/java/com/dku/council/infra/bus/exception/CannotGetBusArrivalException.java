package com.dku.council.infra.bus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotGetBusArrivalException extends LocalizedMessageException {
    public CannotGetBusArrivalException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "failed.get-bus-arrival");
    }

    public CannotGetBusArrivalException(Throwable t) {
        super(t, HttpStatus.INTERNAL_SERVER_ERROR, "failed.get-bus-arrival");
    }
}