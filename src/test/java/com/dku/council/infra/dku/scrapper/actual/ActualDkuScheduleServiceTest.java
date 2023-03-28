package com.dku.council.infra.dku.scrapper.actual;

import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.ScheduleInfo;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuScheduleService;
import com.dku.council.util.WebClientUtil;
import com.dku.council.util.YamlProperties;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;

class ActualDkuScheduleServiceTest {

    private static YamlProperties properties;
    private DkuAuthenticationService authService;
    private DkuScheduleService service;

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
                .clientConnector(WebClientUtil.logger())
                .build();

        this.id = properties.get("dku.static-crawler.id");
        this.password = properties.get("dku.static-crawler.password");

        String webinfoLoginPath = properties.get("dku.login.webinfo-api-path");
        String portalLoginPath = properties.get("dku.login.portal-api-path");
        String schedulePath = properties.get("dku.schedule.api-path");

        this.authService = new DkuAuthenticationService(webClient, webinfoLoginPath, portalLoginPath);
        this.service = new DkuScheduleService(webClient, schedulePath);
    }

    @Test
    @Disabled
    @DisplayName("학사일정 스크래핑 테스트")
    public void actualRetrieveSchedules() {
        DkuAuth auth = authService.loginWebInfo(id, password);
        LocalDate from = LocalDate.of(2023, 3, 1);
        LocalDate to = LocalDate.of(2023, 3, 31);
        List<ScheduleInfo> schedules = service.crawlSchedule(auth, from, to);

        for (ScheduleInfo schedule : schedules) {
            System.out.printf("%s: %s ~ %s\n", schedule.getTitle(), schedule.getFromDate().toString(), schedule.getToDate().toString());
        }
    }
}