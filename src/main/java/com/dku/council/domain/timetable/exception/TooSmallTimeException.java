package com.dku.council.domain.timetable.exception;

import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class TooSmallTimeException extends LocalizedMessageException {

    public TooSmallTimeException(TimePromise promise) {
        super(HttpStatus.BAD_REQUEST, "invalid.too-small-time", promise);
    }
}
