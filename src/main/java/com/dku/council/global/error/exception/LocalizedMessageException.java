package com.dku.council.global.error.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Locale;

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

    public LocalizedMessageException(Throwable cause, HttpStatus status, String messageId, Object... arguments) {
        super(messageId, cause);
        this.status = status;
        this.messageId = messageId;
        this.arguments = arguments;
    }

    public List<Object> getMessages(MessageSource messageSource, Locale locale) {
        return List.of(messageSource.getMessage(messageId, arguments, locale));
    }
}
