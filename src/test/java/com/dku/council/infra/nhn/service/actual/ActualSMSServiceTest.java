package com.dku.council.infra.nhn.service.actual;

import com.dku.council.infra.nhn.service.SMSService;
import com.dku.council.util.WebClientUtil;
import com.dku.council.util.YamlProperties;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

class ActualSMSServiceTest {

    private static YamlProperties properties;
    private SMSService service;


    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new YamlProperties();
        properties.load();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.builder()
                .clientConnector(WebClientUtil.logger())
                .build();
        String apiPath = properties.get("nhn.sms.api-path");
        String secretKey = properties.get("nhn.sms.secret-key");
        String senderPhone = properties.get("nhn.sms.sender-phone");
        this.service = new SMSService(webClient, apiPath, secretKey, senderPhone);
    }

    @Test
    @Disabled
    @DisplayName("실제로 sms를 요청해본다.")
    public void actualSentSms() {
        // 테스트해보고 번호는 지울 것. github에 올라가면 개인정보 누출 문제 발생.
        this.service.sendSMS("", "[인증] Hello, world!");
    }
}