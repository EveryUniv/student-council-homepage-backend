package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyHomeBusCancelRequestException extends LocalizedMessageException {
    public AlreadyHomeBusCancelRequestException() {
        super(HttpStatus.BAD_REQUEST, "already.homebus-cancel");
    }
}
