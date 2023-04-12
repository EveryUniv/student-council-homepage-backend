package com.dku.council.domain.timetable.exception;

import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class TimeConflictException extends LocalizedMessageException {

    public TimeConflictException(TimePromise a, TimePromise b) {
        super(HttpStatus.BAD_REQUEST, "invalid.time-conflict", a, b);
    }
}
