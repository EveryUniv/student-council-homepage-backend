package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyInStorageException extends LocalizedMessageException {
    public AlreadyInStorageException() {
        super(HttpStatus.BAD_REQUEST, "already.in-storage");
    }

    public AlreadyInStorageException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "already.in-storage");
    }
}
