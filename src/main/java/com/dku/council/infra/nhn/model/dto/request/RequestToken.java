package com.dku.council.infra.nhn.model.dto.request;

import lombok.Getter;

@Getter
public class RequestToken {
    private final Auth auth;

    public RequestToken(Auth auth) {
        this.auth = auth;
    }

    @Getter
    public static class Auth {
        private final String tenantId;
        private final PasswordCredentials passwordCredentials;

        public Auth(String tenantId, PasswordCredentials passwordCredentials) {
            this.tenantId = tenantId;
            this.passwordCredentials = passwordCredentials;
        }
    }

    @Getter
    public static class PasswordCredentials {
        private final String username;
        private final String password;

        public PasswordCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}