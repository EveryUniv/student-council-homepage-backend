package com.dku.council.infra.dku.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class DkuFailedCrawlingException extends LocalizedMessageException {

    public DkuFailedCrawlingException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.dku-crawling");
    }

    public DkuFailedCrawlingException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
        setCustomMessage(message);
    }
}