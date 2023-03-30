package com.dku.council.domain.post.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class DuplicateAgreementException extends LocalizedMessageException {
    public DuplicateAgreementException() {
        super(HttpStatus.BAD_REQUEST, "already.agreement");
    }
}
