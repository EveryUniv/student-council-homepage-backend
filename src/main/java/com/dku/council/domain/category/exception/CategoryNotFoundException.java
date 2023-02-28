package com.dku.council.domain.category.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends LocalizedMessageException {

    public CategoryNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.category");
    }
}
