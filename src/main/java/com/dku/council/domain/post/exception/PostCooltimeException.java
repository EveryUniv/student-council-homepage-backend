package com.dku.council.domain.post.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class PostCooltimeException extends LocalizedMessageException {
    public PostCooltimeException(String postType) {
        super(HttpStatus.BAD_REQUEST, "cooltime." + postType);
    }
}