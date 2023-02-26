package com.dku.council.infra.dku.service;

import com.dku.council.infra.dku.exception.DkuFailedLoginException;
import com.dku.council.infra.dku.model.DkuAuth;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DkuAuthenticationServiceTest {

    private static MockWebServer mockServer;
    private DkuAuthenticationService service;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    void beforeEach() {
        WebClient webClient = WebClient.create();
        this.service = new DkuAuthenticationService(webClient, "http://localhost:" + mockServer.getPort());
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("정상적인 흐름일 때 login성공?")
    public void login() {
        // given
        mockServer.enqueue(new MockResponse()
                .setResponseCode(302)
                .addHeader("Location", "http://localhost:" + mockServer.getPort())
                .addHeader("Set-Cookie", "cookie1=value1")
                .addHeader("Set-Cookie", "cookie2=value2"));
        mockServer.enqueue(new MockResponse()
                .setResponseCode(302)
                .addHeader("Set-Cookie", "cookie3=value3")
                .addHeader("Set-Cookie", "cookie4=value4"));

        // when
        DkuAuth auth = service.login("32111111", "pwd");
        MultiValueMap<String, String> actualCookies = new LinkedMultiValueMap<>();
        auth.authCookies().accept(actualCookies);

        // then
        Assertions.assertThat(actualCookies)
                .containsEntry("cookie1", List.of("value1"))
                .containsEntry("cookie2", List.of("value2"))
                .containsEntry("cookie3", List.of("value3"))
                .containsEntry("cookie4", List.of("value4"));
    }

    @Test
    @DisplayName("실패 - 302가 아닌 응답")
    public void failedByNot302StatusCode() {
        // given
        mockServer.enqueue(new MockResponse());

        // when & then
        assertThrows(DkuFailedLoginException.class, () ->
                service.login("32111111", "pwd"));
    }

    @Test
    @DisplayName("실패 - sso로그인에서 302가 아닌 응답")
    public void failedSSOByNot302StatusCode() {
        // given
        mockServer.enqueue(new MockResponse().setResponseCode(302)
                .addHeader("Location", "http://localhost:" + mockServer.getPort()));
        mockServer.enqueue(new MockResponse());

        // when & then
        assertThrows(DkuFailedLoginException.class, () ->
                service.login("32111111", "pwd"));
    }
}