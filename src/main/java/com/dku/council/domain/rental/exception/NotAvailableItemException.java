package com.dku.council.domain.rental.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotAvailableItemException extends LocalizedMessageException {
    public NotAvailableItemException() {
        super(HttpStatus.NOT_FOUND, "notfound.rental-item-available");
    }
}