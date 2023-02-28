package com.dku.council.domain.category.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CategoryIntegrityException extends LocalizedMessageException {

    public CategoryIntegrityException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "integrity.category");
    }
}
