package com.dku.council.infra.dku.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Locale;

// TODO 개선 필요
public class DkuFailedCrawlingException extends LocalizedMessageException {

    private String customMessage = null;

    public DkuFailedCrawlingException() {
        super(HttpStatus.BAD_REQUEST, "failed.dku-crawling");
    }

    public DkuFailedCrawlingException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.dku-crawling");
    }

    public DkuFailedCrawlingException(String message) {
        super(HttpStatus.BAD_REQUEST, "failed.dku-crawling");
        this.customMessage = message;
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
