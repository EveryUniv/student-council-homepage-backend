package com.dku.council.global.error.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BindingFailedException extends LocalizedMessageException {
    public BindingFailedException(BindException e) {
        super(e, HttpStatus.BAD_REQUEST, "");
    }

    @Override
    public List<Object> getMessages(MessageSource messageSource, Locale locale) {
        return ((BindException) getCause()).getFieldErrors().stream()
                .map((err) -> FieldErrorResult.create(err, messageSource, locale))
                .collect(Collectors.toList());
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class FieldErrorResult {
        private final String name;
        private final String error;

        public static FieldErrorResult create(FieldError error, MessageSource messageSource, Locale locale) {
            return new FieldErrorResult(error.getField(), messageSource.getMessage(error, locale));
        }
    }
}
