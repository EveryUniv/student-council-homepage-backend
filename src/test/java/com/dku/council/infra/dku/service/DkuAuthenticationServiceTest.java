package com.dku.council.infra.dku.service;

import com.dku.council.util.WebClientUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

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
}