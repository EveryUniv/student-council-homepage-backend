package com.dku.council.infra.naver.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class UnissuedCaptchaException extends LocalizedMessageException {
    public UnissuedCaptchaException() {
        super(HttpStatus.BAD_REQUEST, "failed.unissued-image");
    }
}
