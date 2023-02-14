package com.dku.council.global.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LocalizedMessageException extends RuntimeException {

    private final HttpStatus status;
    private final String messageId;
    private final Object[] arguments;

    public LocalizedMessageException(HttpStatus status, String messageId, Object... arguments) {
        super(messageId);
        this.status = status;
        this.messageId = messageId;
        this.arguments = arguments;
    }
}
