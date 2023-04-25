package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotAttendingException extends LocalizedMessageException {
    public NotAttendingException() {
        super(HttpStatus.BAD_REQUEST, "invalid.academic-status");
    }
}