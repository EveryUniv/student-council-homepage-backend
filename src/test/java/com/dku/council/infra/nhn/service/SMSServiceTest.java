package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.CannotSendSMSException;
import com.dku.council.infra.nhn.service.impl.SMSServiceImpl;
import com.dku.council.util.base.AbstractMockServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

class SMSServiceTest extends AbstractMockServerTest {

    private SMSService service;

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new SMSServiceImpl(webClient, apiPath, "secretKey", "senderPhone");
    }

    @Test
    @DisplayName("성공 응답 - 정상 처리 여부")
    public void sendSmsSuccess() {
        // given
        mockJson("nhn/sms/response-success");

        // when & then(no error)
        service.sendSMS("01011111111", "Test body");
    }

    @Test
    @DisplayName("실패 응답 - 실패 status code")
    public void failedByBadRequest() {
        // given
        mockJson(HttpStatus.BAD_REQUEST, "nhn/sms/response-success");

        // when & then(no error)
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - Header code 0이 아닌 값")
    public void failedByHeaderCodeNotZero() {
        // given
        mockJson("nhn/sms/response-fail1");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body code 0이 아닌 값")
    public void failedByBodyCodeNotZero() {
        // given
        mockJson("nhn/sms/response-fail2");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - header가 null인 경우")
    public void failedByNullHeader() {
        // given
        mockJson("nhn/sms/response-fail3");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body가 null인 경우")
    public void failedByNullBody() {
        // given
        mockJson("nhn/sms/response-fail4");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }

    @Test
    @DisplayName("실패 응답 - body가 일부 누락된 경우")
    public void failedByInvalidBody() {
        // given
        mockJson("nhn/sms/response-fail5");

        // when & then
        Assertions.assertThrows(CannotSendSMSException.class, () ->
                service.sendSMS("01011111111", "Test body"));
    }
}