package com.dku.council.domain.tag.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class TagNotFoundException extends LocalizedMessageException {

    public TagNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.tag");
    }
}
