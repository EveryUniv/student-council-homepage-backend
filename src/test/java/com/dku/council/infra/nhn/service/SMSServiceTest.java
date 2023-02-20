package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.CannotSendSMSException;
import com.dku.council.infra.nhn.service.impl.SMSServiceImpl;
import com.dku.council.util.MockServerUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

class SMSServiceTest {

    private static MockWebServer mockServer;
    private SMSService service;


    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new SMSServiceImpl(webClient, apiPath, "secretKey", "senderPhone");
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("성공 응답 - 정상 처리 여부")
    public void sendSmsSuccess() {
        // given
        MockServerUtil.jsonBody(mockServer, "nhn/sms/response-success");

        // when & then(no error)
        service.sendSMS("01011111111", "Test body");
    }

    @Test
    @DisplayName("실패 응답 - 실패 status code")
    public void failedByBadRequest() {
        // given
        MockServerUtil.jsonBody(mockServer, HttpStatus.BAD_REQUEST, "nhn/sms/response-success");

        // when & then(no error)
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - Header code 0이 아닌 값")
    public void failedByHeaderCodeNotZero() {
        // given
        MockServerUtil.jsonBody(mockServer, "nhn/sms/response-fail1");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body code 0이 아닌 값")
    public void failedByBodyCodeNotZero() {
        // given
        MockServerUtil.jsonBody(mockServer, "nhn/sms/response-fail2");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - header가 null인 경우")
    public void failedByNullHeader() {
        // given
        MockServerUtil.jsonBody(mockServer, "nhn/sms/response-fail3");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body가 null인 경우")
    public void failedByNullBody() {
        // given
        MockServerUtil.jsonBody(mockServer, "nhn/sms/response-fail4");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body가 일부 누락된 경우")
    public void failedByInvalidBody() {
        // given
        MockServerUtil.jsonBody(mockServer, "nhn/sms/response-fail5");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }
}