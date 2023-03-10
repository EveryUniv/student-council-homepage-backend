package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class MajorNotFoundException extends LocalizedMessageException {
    public MajorNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "notfound.major");
    }
}
