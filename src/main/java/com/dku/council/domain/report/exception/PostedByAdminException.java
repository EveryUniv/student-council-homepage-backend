package com.dku.council.domain.report.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class PostedByAdminException extends LocalizedMessageException {
    public PostedByAdminException() {
        super(HttpStatus.BAD_REQUEST, "invalid.report-user");
    }

}
