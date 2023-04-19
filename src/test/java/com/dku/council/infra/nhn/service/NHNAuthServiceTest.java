package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.CannotGetTokenException;
import com.dku.council.infra.nhn.exception.NotInitializedException;
import com.dku.council.util.base.AbstractMockServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class NHNAuthServiceTest extends AbstractMockServerTest {

    private NHNAuthService service;
    private NHNAuthService notInitializedService;

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new NHNAuthService(webClient, apiPath, "tenantId", "username", "password");
        this.notInitializedService = new NHNAuthService(webClient, apiPath, "tenantId", "username", "password");

        // call @postconstruct manually
        Method init = NHNAuthService.class.getDeclaredMethod("initialize");
        init.setAccessible(true);
        init.invoke(service);
    }

    @Test
    @DisplayName("초기화하지 않았을 때 에러 발생")
    public void failedByNotInitialized() {
        Assertions.assertThrows(NotInitializedException.class, () -> notInitializedService.requestToken());
    }

    @Test
    @DisplayName("성공 응답 - 정상 처리 여부")
    public void sendSmsSuccess() {
        // given
        mockJson("nhn/auth/response-success");

        // when & then(no error)
        service.requestToken();
    }

    @Test
    @DisplayName("실패 응답 - 실패 status code")
    public void failedByBadRequest() {
        // given
        mockJson(HttpStatus.BAD_REQUEST, "nhn/auth/response-success");

        // when & then(no error)
        Assertions.assertThrows(CannotGetTokenException.class, () -> service.requestToken());
    }

    @Test
    @DisplayName("실패 응답 - body가 잘못된 경우")
    public void failedByInvalidBody() {
        // given
        mockJson("nhn/auth/response-fail1");

        // when & then
        Assertions.assertThrows(CannotGetTokenException.class, () -> service.requestToken());
    }
}