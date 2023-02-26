package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class WrongPasswordException extends LocalizedMessageException {
    public WrongPasswordException() {
        super(HttpStatus.BAD_REQUEST, "invalid.password");
    }
}
