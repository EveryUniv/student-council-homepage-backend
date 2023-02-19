package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.nhn.exception.CannotSendSMSException;
import com.dku.council.util.MockServerUtil;
import com.dku.council.util.YamlProperties;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class SMSServiceImplTest {

    private static YamlProperties properties;
    private static MockWebServer mockServer;
    private SMSServiceImpl service;


    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        properties = new YamlProperties();
        properties.load();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        String secretKey = properties.get("nhn.sms.secret-key");
        String senderPhone = properties.get("nhn.sms.sender-phone");
        this.service = new SMSServiceImpl(webClient, apiPath, secretKey, senderPhone);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("성공 응답 - 정상 처리 여부")
    public void sendSmsSuccess() {
        // given
        MockServerUtil.jsonBody(mockServer, "sms/response-success");

        // when & then(no error)
        service.sendSMS("01011111111", "Test body");
    }

    @Test
    @DisplayName("실패 응답 - Header code 0이 아닌 값")
    public void failedByHeaderCodeNotZero() {
        // given
        MockServerUtil.jsonBody(mockServer, "sms/response-fail1");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body code 0이 아닌 값")
    public void failedByBodyCodeNotZero() {
        // given
        MockServerUtil.jsonBody(mockServer, "sms/response-fail2");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - header가 null인 경우")
    public void failedByNullHeader() {
        // given
        MockServerUtil.jsonBody(mockServer, "sms/response-fail3");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body가 null인 경우")
    public void failedByNullBody() {
        // given
        MockServerUtil.jsonBody(mockServer, "sms/response-fail4");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }
}