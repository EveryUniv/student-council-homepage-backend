package com.dku.council.infra.naver.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidCaptchaException extends LocalizedMessageException {
    public InvalidCaptchaException() {
        super(HttpStatus.BAD_REQUEST, "invalid.captcha");
    }
}
