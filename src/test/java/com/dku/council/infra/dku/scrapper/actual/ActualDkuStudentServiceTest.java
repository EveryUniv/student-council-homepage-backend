package com.dku.council.infra.dku.scrapper.actual;

import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.StudentDuesStatus;
import com.dku.council.infra.dku.model.StudentInfo;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuStudentService;
import com.dku.council.util.WebClientUtil;
import com.dku.council.util.YamlProperties;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.YearMonth;

class ActualDkuStudentServiceTest {

    private static YamlProperties properties;
    private DkuAuthenticationService authService;
    private DkuStudentService service;

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
        String infoPath = properties.get("dku.student-info.info-api-path");
        String feePath = properties.get("dku.student-info.fee-api-path");

        this.authService = new DkuAuthenticationService(webClient, webinfoLoginPath, infoPath);
        this.service = new DkuStudentService(webClient, infoPath, feePath);
    }

    @Test
    @Disabled
    @DisplayName("학생 정보 스크래핑 테스트")
    public void actualCrawlStudentInfo() {
        DkuAuth auth = authService.loginWebInfo(id, password);

        StudentInfo studentInfo = service.crawlStudentInfo(auth);
        System.out.printf("학생명: %s, 학번: %s, 입학년도: %d, 학과: %s, 학부: %s, 학적상태: %s\n",
                studentInfo.getStudentName(), studentInfo.getStudentId(), studentInfo.getYearOfAdmission(),
                studentInfo.getMajorName(), studentInfo.getDepartmentName(), studentInfo.getStudentState());
    }

    @Test
    @Disabled
    @DisplayName("회비 납부 정보 스크래핑 테스트")
    public void actualCrawlStudentDues() {
        DkuAuth auth = authService.loginWebInfo(id, password);

        StudentDuesStatus duesStatus;
        try {
            duesStatus = service.crawlStudentDues(auth, YearMonth.now());
        } catch (DkuFailedCrawlingException e) {
            duesStatus = StudentDuesStatus.NOT_PAID;
        }
    }
}