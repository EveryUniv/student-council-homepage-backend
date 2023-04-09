package com.dku.council.domain.user.model.dto.response;

import com.dku.council.global.auth.jwt.AuthenticationToken;
import lombok.Getter;

@Getter
public class ResponseLoginDto {
    private final String accessToken;
    private final String refreshToken;

    public ResponseLoginDto(AuthenticationToken token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
