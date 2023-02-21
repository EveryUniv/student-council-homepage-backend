package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.CannotGetTokenException;
import com.dku.council.infra.nhn.exception.NotInitializedException;
import com.dku.council.infra.nhn.service.impl.NHNAuthServiceImpl;
import com.dku.council.util.MockServerUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class NHNAuthServiceTest {

    private static MockWebServer mockServer;
    private NHNAuthService service;
    private NHNAuthService notInitializedService;


    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new NHNAuthServiceImpl(webClient, apiPath, "tenantId", "username", "password");
        this.notInitializedService = new NHNAuthServiceImpl(webClient, apiPath, "tenantId", "username", "password");

        // call @postconstruct manually
        Method init = NHNAuthServiceImpl.class.getDeclaredMethod("initialize");
        init.setAccessible(true);
        init.invoke(service);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
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
        MockServerUtil.jsonBody(mockServer, "nhn/auth/response-success");

        // when & then(no error)
        service.requestToken();
    }

    @Test
    @DisplayName("실패 응답 - 실패 status code")
    public void failedByBadRequest() {
        // given
        MockServerUtil.jsonBody(mockServer, HttpStatus.BAD_REQUEST, "nhn/auth/response-success");

        // when & then(no error)
        Assertions.assertThrows(CannotGetTokenException.class, () -> service.requestToken());
    }

    @Test
    @DisplayName("실패 응답 - body가 잘못된 경우")
    public void failedByInvalidBody() {
        // given
        MockServerUtil.jsonBody(mockServer, "nhn/auth/response-fail1");

        // when & then
        Assertions.assertThrows(CannotGetTokenException.class, () -> service.requestToken());
    }
}