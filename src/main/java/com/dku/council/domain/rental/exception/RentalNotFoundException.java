package com.dku.council.domain.rental.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class RentalNotFoundException extends LocalizedMessageException {
    public RentalNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.rental");
    }
}