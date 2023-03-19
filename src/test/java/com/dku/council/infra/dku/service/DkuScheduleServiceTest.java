package com.dku.council.infra.dku.service;

import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.Schedule;
import com.dku.council.util.base.AbstractMockServerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DkuScheduleServiceTest extends AbstractMockServerTest {

    private DkuScheduleService service;

    @BeforeEach
    void beforeEach() {
        WebClient webClient = WebClient.create();
        this.service = new DkuScheduleService(webClient, "http://localhost:" + mockServer.getPort());
    }

    @Test
    @DisplayName("응답 json으로부터 일정 정보가 잘 parsing되는지?")
    void crawlSchedule() {
        // given
        mockJson("dku/schedule-response");
        List<Schedule> expected = List.of(
                new Schedule("2022학년도 2학기 기말고사 성적 입력 및 제출 기간",
                        LocalDate.of(2022, 12, 13),
                        LocalDate.of(2023, 1, 4)),
                new Schedule("2022학년도 계절(동계)학기 수업기간",
                        LocalDate.of(2022, 12, 26),
                        LocalDate.of(2023, 1, 13)),
                new Schedule("2023학년도 1학기 Web 휴학원서제출",
                        LocalDate.of(2022, 12, 26),
                        LocalDate.of(2023, 2, 28)),
                new Schedule("2022학년도 2학기 기말고사 성적확인 및 공시기간",
                        LocalDate.of(2022, 12, 28),
                        LocalDate.of(2023, 1, 3)),
                new Schedule("2022학년도 계절(동계)학기 수업일수 5-8일",
                        LocalDate.of(2022, 12, 30),
                        LocalDate.of(2023, 1, 4)),
                new Schedule("2022 하반기  천안시 대학생 학자금 대출이자 지원",
                        LocalDate.of(2022, 12, 30),
                        LocalDate.of(2023, 1, 20))
        );

        // when
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        List<Schedule> schedules = service.crawlSchedule(dummyAuth);

        // then
        for (int i = 0; i < expected.size(); i++) {
            Schedule expectedResult = expected.get(i);
            Schedule actual = schedules.get(i);
            assertThat(actual.getTitle()).isEqualTo(expectedResult.getTitle());
            assertThat(actual.getToDate()).isEqualTo(expectedResult.getToDate());
            assertThat(actual.getFromDate()).isEqualTo(expectedResult.getFromDate());
        }
    }

    @Test
    @DisplayName("실패 응답 - not successful")
    public void failedCrawlByNotSuccessfulCode() {
        // given
        mockJson("dku/schedule-response-failed");

        // when & then
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        try {
            service.crawlSchedule(dummyAuth);
        } catch (DkuFailedCrawlingException e) {
            assertThat(e.getMessage()).isEqualTo("데이터를 찾을 수 없습니다.");
        }
    }
}