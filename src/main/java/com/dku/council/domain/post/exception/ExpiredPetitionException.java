package com.dku.council.domain.post.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class ExpiredPetitionException extends LocalizedMessageException {
    public ExpiredPetitionException() {
        super(HttpStatus.BAD_REQUEST, "invalid.expired-petition");
    }
}
