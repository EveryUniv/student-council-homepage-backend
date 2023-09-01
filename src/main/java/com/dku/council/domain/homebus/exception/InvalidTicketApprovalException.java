package com.dku.council.domain.homebus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidTicketApprovalException extends LocalizedMessageException {
    public InvalidTicketApprovalException() {
        super(HttpStatus.BAD_REQUEST, "invalid.ticket-approval");
    }
}
