package com.dku.council.infra.nhn.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidAccessObjectStorageException extends LocalizedMessageException {
    public InvalidAccessObjectStorageException() {
        super(HttpStatus.BAD_REQUEST, "failed.access-object-storage");
    }

    public InvalidAccessObjectStorageException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "failed.access-object-storage");
    }
}
