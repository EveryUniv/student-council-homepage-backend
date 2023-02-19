package com.dku.council.domain.post.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends LocalizedMessageException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.user");
    }
}