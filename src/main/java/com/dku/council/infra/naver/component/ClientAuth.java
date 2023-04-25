package com.dku.council.infra.naver.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ClientAuth {

    @Value("${naver.captcha.client-id}")
    private final String clientId;

    @Value("${naver.captcha.client-secret}")
    private final String clientSecret;

    public WebClient.RequestHeadersSpec<?> addAuthHeader(WebClient.RequestHeadersSpec<?> spec) {
        return spec.header("X-NCP-APIGW-API-KEY-ID", clientId)
                .header("X-NCP-APIGW-API-KEY", clientSecret);
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
