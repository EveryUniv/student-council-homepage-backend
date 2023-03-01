package com.dku.council.domain.post.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class DuplicateCommentException extends LocalizedMessageException {
    public DuplicateCommentException() {
        super(HttpStatus.BAD_REQUEST, "already.comment");
    }
}