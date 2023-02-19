package com.dku.council.infra.nhn.model.dto.response;

import lombok.Getter;

@Getter
public class ResponseToken {
    private final Access access;

    public ResponseToken(Access access) {
        this.access = access;
    }

    public String getTokenId() {
        return access.token.id;
    }

    @Getter
    public static class Access {
        private final Token token;

        public Access(Token token) {
            this.token = token;
        }
    }

    @Getter
    public static class Token {
        private final String id;

        public Token(String id) {
            this.id = id;
        }
    }
}