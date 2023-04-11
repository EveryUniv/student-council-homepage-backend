package com.dku.council.domain.report.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotReportMineException extends LocalizedMessageException{
    public CannotReportMineException() {
        super(HttpStatus.BAD_REQUEST, "failed.report-mypost");
    }
}
