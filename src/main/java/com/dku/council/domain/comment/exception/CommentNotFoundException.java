package com.dku.council.domain.comment.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends LocalizedMessageException {
    public CommentNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.comment");
    }
}