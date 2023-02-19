package com.dku.council.infra.nhn.model.dto.response;

import lombok.Data;

@Data
public class ResponseToken {
    private Access access = new Access();

    public String getTokenId() {
        return access.token.id;
    }

    @Data
    public static class Access {
        private Token token = new Token();
    }

    @Data
    public static class Token {
        private String id;
    }
}