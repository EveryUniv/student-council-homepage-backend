package com.dku.council.infra.dku.service;

import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.StudentDuesStatus;
import com.dku.council.infra.dku.model.StudentInfo;
import com.dku.council.util.base.AbstractMockServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class DkuCrawlerServiceTest extends AbstractMockServerTest {

    private DkuCrawlerService service;

    @BeforeEach
    void beforeEach() {
        WebClient webClient = WebClient.create();
        String path = "http://localhost:" + mockServer.getPort();
        this.service = new DkuCrawlerService(webClient, path, path);
    }

    @Test
    @DisplayName("응답 html로부터 학생 정보가 잘 parsing되는지?")
    public void crawlStudentInfo() {
        // given
        mockHtml("dku/student-info-response");

        // when
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        StudentInfo studentInfo = service.crawlStudentInfo(dummyAuth);

        // then
        assertThat(studentInfo.getStudentName()).isEqualTo("학생명");
        assertThat(studentInfo.getStudentId()).isEqualTo("32220000");
        assertThat(studentInfo.getYearOfAdmission()).isEqualTo(2022);
        assertThat(studentInfo.getMajorName()).isEqualTo("정치외교학과");
        assertThat(studentInfo.getDepartmentName()).isEqualTo("사회과학대학");
        assertThat(studentInfo.getStudentState()).isEqualTo("재학");
    }

    @Test
    @DisplayName("실패 응답 - 실패 status code")
    public void failedCrawlStudentInfoByInvalidStatusCode() {
        // given
        mockWithStatus(HttpStatus.NOT_FOUND);

        // when & then
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        Assertions.assertThrows(DkuFailedCrawlingException.class, () ->
                service.crawlStudentInfo(dummyAuth));
    }

    @Test
    @DisplayName("실패 응답 - 학번정보가 누락된 response")
    public void failedCrawlStudentInfoByInvalidResponse1() {
        // given
        mockHtml("dku/student-info-response-failed-1");

        // when & then
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        Assertions.assertThrows(DkuFailedCrawlingException.class, () ->
                service.crawlStudentInfo(dummyAuth));
    }

    @Test
    @DisplayName("응답 html로부터 학생회비 납부 정보가 잘 parsing되는지?")
    public void crawlStudentDues() {
        // given
        mockHtml("dku/fee-info-response");

        // when
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        StudentDuesStatus status = service.crawlStudentDues(dummyAuth, YearMonth.of(2023, 4));

        // then
        assertThat(status).isEqualTo(StudentDuesStatus.PAID);
    }

    @Test
    @DisplayName("학생회비를 납부하지 않은경우 - 아얘 등록정보가 없음")
    public void crawlStudentDuesNotPaid1() {
        // given
        mockHtml("dku/no-fee-info-response1");

        // when
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        StudentDuesStatus status = service.crawlStudentDues(dummyAuth, YearMonth.of(2023, 4));

        // then
        assertThat(status).isEqualTo(StudentDuesStatus.NOT_PAID);
    }

    @Test
    @DisplayName("학생회비를 납부하지 않은경우 - 등록은 했지만 회비 온전히 내지 않음")
    public void crawlStudentDuesNotPaid2() {
        // given
        mockHtml("dku/no-fee-info-response2");

        // when
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        StudentDuesStatus status = service.crawlStudentDues(dummyAuth, YearMonth.of(2022, 9));

        // then
        assertThat(status).isEqualTo(StudentDuesStatus.NOT_PAID);
    }

    @Test
    @DisplayName("실패 응답 - 실패 status code")
    public void failedCrawlStudentDuesByInvalidStatusCode() {
        // given
        mockWithStatus(HttpStatus.NOT_FOUND);

        // when & then
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        Assertions.assertThrows(DkuFailedCrawlingException.class, () ->
                service.crawlStudentInfo(dummyAuth));
    }
}