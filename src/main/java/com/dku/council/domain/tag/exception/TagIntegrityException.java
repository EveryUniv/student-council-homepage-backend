package com.dku.council.domain.tag.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class TagIntegrityException extends LocalizedMessageException {

    public TagIntegrityException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "integrity.tag");
    }
}
