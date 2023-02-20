package com.dku.council.infra.nhn.model.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ResponseToken {
    private Access access;

    public String getTokenId() {
        return access.token.id;
    }

    @Getter
    @NoArgsConstructor(access = PROTECTED)
    public static class Access {
        private Token token;
    }

    @Getter
    @NoArgsConstructor(access = PROTECTED)
    public static class Token {
        private String id;
    }
}