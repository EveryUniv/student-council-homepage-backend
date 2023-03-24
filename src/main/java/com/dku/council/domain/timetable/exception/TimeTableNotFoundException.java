package com.dku.council.domain.timetable.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class TimeTableNotFoundException extends LocalizedMessageException {

    public TimeTableNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.timetable");
    }
}
