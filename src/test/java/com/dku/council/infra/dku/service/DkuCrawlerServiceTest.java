package com.dku.council.infra.dku.service;

import com.dku.council.domain.user.model.MajorData;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.StudentInfo;
import com.dku.council.util.MockServerUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DkuCrawlerServiceTest {

    @Mock
    MessageSource messageSource;

    private static MockWebServer mockServer;
    private DkuCrawlerService service;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    void beforeEach() {
        WebClient webClient = WebClient.create();
        this.service = new DkuCrawlerService(webClient, messageSource,
                "http://localhost:" + mockServer.getPort());
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("응답 html로부터 학생 정보가 잘 parsing되는지?")
    public void crawlStudentInfo() {
        // given
        when(messageSource.getMessage(Mockito.startsWith("Major."), any(), any())).thenAnswer((invo) -> {
            if (invo.getArgument(0).equals("Major.POLITICAL")) {
                return "정치외교학과";
            } else {
                return "모르는 학과";
            }
        });
        MockServerUtil.html(mockServer, "dku/student-info-response");

        // when
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        StudentInfo studentInfo = service.crawlStudentInfo(dummyAuth);

        // then
        assertThat(studentInfo.getStudentName()).isEqualTo("학생명");
        assertThat(studentInfo.getStudentId()).isEqualTo("32220000");
        assertThat(studentInfo.getYearOfAdmission()).isEqualTo(2022);
        assertThat(studentInfo.getMajorData()).isEqualTo(MajorData.POLITICAL);
    }

    @Test
    @DisplayName("실패 응답 - 실패 status code")
    public void failedCrawlByInvalidStatusCode() {
        // given
        MockServerUtil.status(mockServer, HttpStatus.NOT_FOUND);

        // when & then
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        Assertions.assertThrows(DkuFailedCrawlingException.class, () ->
                service.crawlStudentInfo(dummyAuth));
    }

    @Test
    @DisplayName("실패 응답 - 학번정보가 누락된 response")
    public void failedCrawlByInvalidResponse1() {
        // given
        MockServerUtil.html(mockServer, "dku/student-info-response-failed-1");

        // when & then
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        Assertions.assertThrows(DkuFailedCrawlingException.class, () ->
                service.crawlStudentInfo(dummyAuth));
    }

    @Test
    @DisplayName("실패 응답 - 없는 학과인 response")
    public void failedCrawlByInvalidResponse2() {
        // given
        MockServerUtil.html(mockServer, "dku/student-info-response-failed-2");

        // when & then
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        Assertions.assertThrows(DkuFailedCrawlingException.class, () ->
                service.crawlStudentInfo(dummyAuth));
    }
}