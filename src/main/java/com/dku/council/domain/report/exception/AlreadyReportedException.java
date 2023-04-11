package com.dku.council.domain.report.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyReportedException extends LocalizedMessageException {
    public AlreadyReportedException() {
        super(HttpStatus.BAD_REQUEST, "already.reported");
    }
}
