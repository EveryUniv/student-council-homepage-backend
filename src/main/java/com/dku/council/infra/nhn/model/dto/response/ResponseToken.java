package com.dku.council.infra.nhn.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@RequiredArgsConstructor(access = PROTECTED)
public class ResponseToken {
    private final Access access;

    @Getter
    @RequiredArgsConstructor(access = PROTECTED)
    public static class Access {
        private final Token token;
    }

    @Getter
    @RequiredArgsConstructor(access = PROTECTED)
    public static class Token {
        private final String id;
    }
}