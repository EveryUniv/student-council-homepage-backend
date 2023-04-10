package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidFileContentTypeException extends LocalizedMessageException {
    public InvalidFileContentTypeException(Throwable e) {
        super(e, HttpStatus.BAD_REQUEST, "invalid.file-content-type");
    }
}
