package com.dku.council.domain.bus.holiday.exception;

public class CannotGetHolidays extends RuntimeException {

    public CannotGetHolidays(Throwable e) {
        super(e);
    }

    public CannotGetHolidays(String s) {
        super(s);
    }
}