package com.dku.council.infra.naver.service;

import com.dku.council.infra.naver.component.CaptchaApiPath;
import com.dku.council.infra.naver.component.ClientAuth;
import com.dku.council.infra.naver.exception.CannotRequestCaptchaException;
import com.dku.council.infra.naver.exception.InvalidCaptchaException;
import com.dku.council.infra.naver.exception.InvalidCaptchaKeyException;
import com.dku.council.util.base.AbstractMockServerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CaptchaServiceTest extends AbstractMockServerTest {

    private CaptchaService service;
    private CaptchaService disabledService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        ClientAuth clientAuth = new ClientAuth("clientId", "clientSecret");
        String dummyPath = "http://localhost:" + mockServer.getPort();

        CaptchaApiPath apiPath = new CaptchaApiPath(clientAuth, dummyPath, dummyPath, dummyPath);
        service = new CaptchaService(true, objectMapper, apiPath, webClient);
        disabledService = new CaptchaService(false, objectMapper, apiPath, webClient);
    }

    @Test
    @DisplayName("캡차 키를 요청한다.")
    void requestCaptchaKey() {
        // given
        mockPlain("naver/captcha/key.txt");

        // when
        String key = service.requestCaptchaKey();

        // then
        assertThat(key).isEqualTo("0nNrJtJMq5KbeQGu");
    }

    @Test
    @DisplayName("캡차 키 요청 - 실패 응답")
    void failedRequestCaptchaKeyByInvalidStatus() {
        // given
        mockWithStatus(HttpStatus.BAD_REQUEST);

        // when & then
        assertThrows(CannotRequestCaptchaException.class, () -> service.requestCaptchaKey());
    }

    @Test
    @DisplayName("캡차 이미지 요청")
    void requestCaptchaImage() {
        // given
        try (Buffer b = new Buffer()) {
            b.write(new byte[]{1, 2, 3});
            mockServer.enqueue(new MockResponse()
                    .setBody(b)
                    .addHeader("Content-Type", MediaType.IMAGE_JPEG_VALUE));
        }

        // when
        byte[] image = service.requestCaptchaImage("key");

        // then
        assertThat(image).containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("캡차 이미지 요청 - 키를 틀린 경우")
    void requestCaptchaImageInvalidKey() {
        // given
        mockPlain(HttpStatus.BAD_REQUEST, "naver/captcha/invalid-key.txt");

        // when & then
        assertThrows(InvalidCaptchaKeyException.class, () -> service.requestCaptchaImage(""));
    }

    @Test
    @DisplayName("캡차 이미지 요청 - 실패 응답")
    void failedRequestCaptchaImageByInvalidStatus() {
        // given
        mockWithStatus(HttpStatus.BAD_REQUEST);

        // when & then
        assertThrows(CannotRequestCaptchaException.class, () -> service.requestCaptchaImage(""));
    }

    @Test
    @DisplayName("캡차 검증")
    void verifyCaptcha() {
        // given
        mockPlain("naver/captcha/verify-success.txt");

        // when & then
        service.verifyCaptcha("key", "value");
    }

    @Test
    @DisplayName("캡차 검증 - 틀린 캡차인 경우")
    void verifyCaptchaFailed() {
        // given
        mockPlain("naver/captcha/verify-fail.txt");

        // when & then
        assertThrows(InvalidCaptchaException.class, () -> service.verifyCaptcha("key", "value"));
    }

    @Test
    @DisplayName("캡차 검증 - disabled인 경우 검증 패스")
    void disabledVerifyCaptcha() {
        // given
        mockPlain("naver/captcha/verify-fail.txt");

        // when & then
        disabledService.verifyCaptcha("", "");
    }

    @Test
    @DisplayName("캡차 검증 - 키를 틀린 경우")
    void requestVerifyCaptchaInvalidKey() {
        // given
        mockPlain(HttpStatus.BAD_REQUEST, "naver/captcha/invalid-key.txt");

        // when & then
        assertThrows(InvalidCaptchaKeyException.class, () -> service.verifyCaptcha("", ""));
    }

    @Test
    @DisplayName("캡차 검증 - 실패 응답")
    void failedVerifyCaptchaByInvalidStatus() {
        // given
        mockWithStatus(HttpStatus.BAD_REQUEST);

        // when & then
        assertThrows(CannotRequestCaptchaException.class, () -> service.verifyCaptcha("", ""));
    }
}