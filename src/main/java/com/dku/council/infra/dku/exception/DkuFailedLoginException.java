package com.dku.council.infra.dku.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Locale;

public class DkuFailedLoginException extends LocalizedMessageException {

    private String customMessage = null;

    public DkuFailedLoginException() {
        super(HttpStatus.BAD_REQUEST, "failed.dku-login");
    }

    public DkuFailedLoginException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.dku-login");
    }

    public DkuFailedLoginException(String customMessage) {
        super(HttpStatus.BAD_REQUEST, "failed.dku-login");
        this.customMessage = customMessage;
    }

    @Override
    public List<Object> getMessages(MessageSource messageSource, Locale locale) {
        if (customMessage != null) {
            return List.of(customMessage);
        }
        return super.getMessages(messageSource, locale);
    }

    @Override
    public String getMessage() {
        if (customMessage != null) {
            return customMessage;
        }
        return super.getMessage();
    }
}
