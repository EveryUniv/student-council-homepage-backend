package com.dku.council.infra.bus.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class CannotGetTimeTable extends RuntimeException {

    public CannotGetTimeTable(Throwable e) {
        super(e);
    }

    public CannotGetTimeTable(String s) {
        super(s);
    }
}