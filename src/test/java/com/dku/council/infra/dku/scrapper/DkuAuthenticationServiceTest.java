package com.dku.council.infra.dku.scrapper;

import com.dku.council.infra.dku.exception.DkuFailedLoginException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.util.base.AbstractMockServerTest;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DkuAuthenticationServiceTest extends AbstractMockServerTest {

    private DkuAuthenticationService service;

    @BeforeEach
    void beforeEach() {
        WebClient webClient = WebClient.create();
        String url = "http://localhost:" + mockServer.getPort();
        this.service = new DkuAuthenticationService(webClient, url, url);
    }

    @Test
    @DisplayName("정상적인 흐름일 때 login성공")
    public void login() {
        // given
        int prevCount = mockServer.getRequestCount();
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
        DkuAuth auth = service.loginWebInfo("32111111", "pwd");
        MultiValueMap<String, String> actualCookies = new LinkedMultiValueMap<>();
        auth.authCookies().accept(actualCookies);

        // then
        assertThat(actualCookies)
                .containsEntry("cookie1", List.of("value1"))
                .containsEntry("cookie2", List.of("value2"))
                .containsEntry("cookie3", List.of("value3"))
                .containsEntry("cookie4", List.of("value4"));
        assertThat(mockServer.getRequestCount() - prevCount).isEqualTo(2);
    }

    @Test
    @DisplayName("실패 - 302가 아닌 응답")
    public void failedByNot302StatusCode() {
        // given
        mockServer.enqueue(new MockResponse());

        // when & then
        assertThrows(DkuFailedLoginException.class, () ->
                service.loginWebInfo("32111111", "pwd"));
    }

    @Test
    @DisplayName("실패 - 실패 메시지 추출")
    public void failedAndExtractMessage() {
        // given
        mockHtml("dku/failed-login-response");

        try {
            // when
            service.loginWebInfo("32111111", "pwd");
        } catch (DkuFailedLoginException e) {
            // then
            assertThat(e.getMessage()).isEqualTo("회원 아이디 또는 비밀번호가 일치하지 않습니다.");
        }
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
                service.loginWebInfo("32111111", "pwd"));
    }
}