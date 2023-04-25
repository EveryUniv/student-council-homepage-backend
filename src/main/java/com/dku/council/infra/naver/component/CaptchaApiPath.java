package com.dku.council.infra.naver.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CaptchaApiPath {

    private final ClientAuth clientAuth;

    @Value("${naver.captcha.key-request.api-path}")
    private final String keyRequestApiPath;

    @Value("${naver.captcha.image-request.api-path}")
    private final String imageRequestApiPath;

    @Value("${naver.captcha.validation.api-path}")
    private final String validationApiPath;

    public WebClient.RequestHeadersSpec<?> requestKey(WebClient.RequestHeadersUriSpec<?> spec) {
        WebClient.RequestHeadersSpec<?> result = spec.uri(keyRequestApiPath);
        result = clientAuth.addAuthHeader(result);
        return result;
    }

    public WebClient.RequestHeadersSpec<?> requestImage(WebClient.RequestHeadersUriSpec<?> spec, String key) {
        String uri = String.format(imageRequestApiPath, key, clientAuth.getClientId());
        return spec.uri(uri);
    }

    public WebClient.RequestHeadersSpec<?> requestValidation(WebClient.RequestHeadersUriSpec<?> spec,
                                                             String key, String value) {
        String uri = String.format(validationApiPath, key, value);
        WebClient.RequestHeadersSpec<?> result = spec.uri(uri);
        result = clientAuth.addAuthHeader(result);
        return result;
    }
}
