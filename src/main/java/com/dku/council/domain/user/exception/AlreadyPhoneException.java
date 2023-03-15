package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyPhoneException extends LocalizedMessageException {
    public AlreadyPhoneException() {
        super(HttpStatus.BAD_REQUEST, "already.phone");
    }
}