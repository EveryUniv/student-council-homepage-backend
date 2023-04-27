package com.dku.council.infra.naver.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotRequestCaptchaException extends LocalizedMessageException {

    public CannotRequestCaptchaException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.captcha");
    }
}
