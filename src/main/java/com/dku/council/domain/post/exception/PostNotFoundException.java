package com.dku.council.domain.post.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends LocalizedMessageException {
    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.post");
    }
}