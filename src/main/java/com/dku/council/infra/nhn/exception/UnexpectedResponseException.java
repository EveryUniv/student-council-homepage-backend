package com.dku.council.infra.nhn.exception;

public class UnexpectedResponseException extends RuntimeException {
    public UnexpectedResponseException(String message) {
        super(message);
    }
}
