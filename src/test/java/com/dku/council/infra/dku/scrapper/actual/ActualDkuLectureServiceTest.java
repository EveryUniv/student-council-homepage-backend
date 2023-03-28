package com.dku.council.infra.dku.scrapper.actual;

import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.Subject;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuLectureService;
import com.dku.council.util.YamlProperties;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ActualDkuLectureServiceTest {

    private static YamlProperties properties;
    private DkuAuthenticationService authService;
    private DkuLectureService service;

    private String id;
    private String password;


    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new YamlProperties();
        properties.load();
    }

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.builder()
                .build();

        this.id = properties.get("dku.static-crawler.id");
        this.password = properties.get("dku.static-crawler.password");

        String webinfoLoginPath = properties.get("dku.login.webinfo-api-path");
        String portalLoginPath = properties.get("dku.login.portal-api-path");
        String path = properties.get("dku.lecture.api-path");

        this.authService = new DkuAuthenticationService(webClient, webinfoLoginPath, portalLoginPath);
        this.service = new DkuLectureService(webClient, path);
    }

    @Test
    @DisplayName("강의 정보 스크래핑 테스트")
    @Disabled
    public void actualRetrieveLecture() {
        DkuAuth auth = authService.loginWebInfo(id, password);

        List<Subject> subjects = service.crawlLecture(auth, YearMonth.now());

        assertThat(subjects).hasSize(5415);
    }
}