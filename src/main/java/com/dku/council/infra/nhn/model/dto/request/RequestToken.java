package com.dku.council.infra.nhn.model.dto.request;

import lombok.Data;

@Data
public class RequestToken {
    private Auth auth = new Auth();

    @Data
    public static class Auth {
        private String tenantId;
        private PasswordCredentials passwordCredentials = new PasswordCredentials();
    }

    @Data
    public static class PasswordCredentials {
        private String username;
        private String password;
    }
}