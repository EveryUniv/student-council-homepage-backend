package com.dku.council.global.auth.jwt;

import org.springframework.security.core.Authentication;

// TODO Guest도 authentication 넣기
public interface AppAuthentication extends Authentication {

    Long getUserId();

    boolean isAdmin();
}
