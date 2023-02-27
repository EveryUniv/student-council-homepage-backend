package com.dku.council.global.auth.jwt;

import org.springframework.security.core.Authentication;

public interface AppAuthentication extends Authentication {
    Long getUserId();

    String getUserRole();

    boolean isAdmin();
}
