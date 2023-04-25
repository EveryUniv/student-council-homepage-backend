package com.dku.council.infra.naver.service.actual;

import com.dku.council.infra.naver.component.CaptchaApiPath;
import com.dku.council.infra.naver.component.ClientAuth;
import com.dku.council.infra.naver.service.CaptchaService;
import com.dku.council.util.WebClientUtil;
import com.dku.council.util.YamlProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class ActualCaptchaServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private YamlProperties properties;
    private CaptchaService service;


    @BeforeEach
    public void beforeEach() throws Throwable {
        properties = new YamlProperties();
        properties.load();

        WebClient webClient = WebClient.builder()
                .clientConnector(WebClientUtil.logger())
                .build();

        String clientId = properties.get("naver.captcha.client-id");
        String clientSecret = properties.get("naver.captcha.client-secret");
        ClientAuth clientAuth = new ClientAuth(clientId, clientSecret);

        String keyRequest = properties.get("naver.captcha.key-request.api-path");
        String imageRequest = properties.get("naver.captcha.image-request.api-path");
        String validation = properties.get("naver.captcha.validation.api-path");
        CaptchaApiPath captchaApiPath = new CaptchaApiPath(clientAuth, keyRequest, imageRequest, validation);

        service = new CaptchaService(true, objectMapper, captchaApiPath, webClient);
    }

    @Test
    @Disabled
    @DisplayName("실제로 키 발급해보기")
    public void actualRequestKey() {
        String key = service.requestCaptchaKey();
        System.out.println(key);
    }

    @Test
    @Disabled
    @DisplayName("실제로 이미지 발급해보기")
    public void actualRequestImage() {
        byte[] data = service.requestCaptchaImage("JzlM3GhHU3WavT2M");
        System.out.println(data.length);
    }

    @Test
    @Disabled
    @DisplayName("실제로 코드 검증해보기")
    public void actualValidation() {
        service.verifyCaptcha("JzlM3GhHU3WavT2M", "55");
        System.out.println("OK");
    }
}