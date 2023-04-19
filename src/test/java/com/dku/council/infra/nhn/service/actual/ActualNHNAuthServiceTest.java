package com.dku.council.infra.nhn.service.actual;

import com.dku.council.infra.nhn.service.NHNAuthService;
import com.dku.council.util.WebClientUtil;
import com.dku.council.util.YamlProperties;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ActualNHNAuthServiceTest {

    private static YamlProperties properties;
    private NHNAuthService service;


    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new YamlProperties();
        properties.load();
    }

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.builder()
                .clientConnector(WebClientUtil.logger())
                .build();

        String apiPath = properties.get("nhn.auth.api-path");
        String tenantId = properties.get("nhn.auth.tenant-id");
        String username = properties.get("nhn.auth.username");
        String password = properties.get("nhn.auth.password");

        this.service = new NHNAuthService(webClient, apiPath, tenantId, username, password);

        // call @postconstruct manually
        Method init = NHNAuthService.class.getDeclaredMethod("initialize");
        init.setAccessible(true);
        init.invoke(service);
    }

    @Test
    @Disabled
    @DisplayName("실제로 token을 발급받아본다.")
    public void actualRetrieveToken() {
        System.out.println(this.service.requestToken());
    }
}