package com.dku.council.domain.rental.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyRentalException extends LocalizedMessageException {
    public AlreadyRentalException() {
        super(HttpStatus.NOT_FOUND, "already.rental");
    }
}