package com.dku.council.infra.naver.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidCaptchaKeyException extends LocalizedMessageException {
    public InvalidCaptchaKeyException() {
        super(HttpStatus.BAD_REQUEST, "invalid.captcha-key");
    }
}
