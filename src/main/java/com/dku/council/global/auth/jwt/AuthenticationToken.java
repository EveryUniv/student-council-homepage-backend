package com.dku.council.global.auth.jwt;

public interface AuthenticationToken {
    String getAccessToken();

    String getRefreshToken();
}
