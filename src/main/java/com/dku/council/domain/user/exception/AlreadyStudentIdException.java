package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyStudentIdException extends LocalizedMessageException {
    public AlreadyStudentIdException() {
        super(HttpStatus.NOT_FOUND, "already.student-id");
    }
}