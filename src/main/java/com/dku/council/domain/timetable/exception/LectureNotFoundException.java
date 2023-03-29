package com.dku.council.domain.timetable.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class LectureNotFoundException extends LocalizedMessageException {

    public LectureNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.lecture");
    }
}
