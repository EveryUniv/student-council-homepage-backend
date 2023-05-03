package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class IllegalNicknameException extends LocalizedMessageException {
    public IllegalNicknameException() {
        super(HttpStatus.BAD_REQUEST, "invalid.nickname");
    }
}