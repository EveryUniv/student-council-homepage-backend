package com.dku.council.infra.dku.scrapper;

import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.MajorSubject;
import com.dku.council.infra.dku.model.Subject;
import com.dku.council.util.base.AbstractMockServerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DkuLectureServiceTest extends AbstractMockServerTest {

    private DkuLectureService service;

    @BeforeEach
    void beforeEach() {
        WebClient webClient = WebClient.create();
        String path = "http://localhost:" + mockServer.getPort();
        this.service = new DkuLectureService(webClient, path);
    }

    @Test
    void crawlLecture() {
        // given
        mockHtml("dku/lecture-subject-response");
        mockHtml("dku/lecture-major-response");

        List<Subject> expected = new ArrayList<>();
        expected.addAll(dummySubject());
        expected.addAll(dummyMajorSubject());

        // when
        DkuAuth dummyAuth = new DkuAuth(new LinkedMultiValueMap<>());
        List<Subject> subjects = service.crawlLecture(dummyAuth, YearMonth.of(2022, 3));

        // then
        assertThat(subjects).containsExactlyInAnyOrderElementsOf(expected);
        System.out.println(subjects);
    }

    private static List<Subject> dummySubject() {
        List<Subject> result = new ArrayList<>();

        result.add(Subject.builder()
                .category("세계시민역량")
                .id("539250")
                .classNumber(1)
                .name("대학영어1([초급]문과대학)")
                .credit(3)
                .professor("최희영")
                .times(List.of(
                        new Subject.TimeAndPlace(
                                DayOfWeek.TUESDAY,
                                LocalTime.of(16, 0),
                                LocalTime.of(17, 30),
                                null
                        )
                ))
                .build());

        result.add(Subject.builder()
                .category("세계시민역량")
                .id("539250")
                .classNumber(2)
                .name("대학영어1([초급]문과대학)")
                .credit(3)
                .professor("김금선")
                .times(List.of(
                        new Subject.TimeAndPlace(
                                DayOfWeek.TUESDAY,
                                LocalTime.of(16, 0),
                                LocalTime.of(17, 30),
                                null
                        )
                ))
                .build());

        result.add(Subject.builder()
                .category("세계시민역량")
                .id("539250")
                .classNumber(3)
                .name("대학영어1([중급]문과대학)")
                .credit(3)
                .professor("찰스코퍼랜드")
                .times(List.of(
                        new Subject.TimeAndPlace(
                                DayOfWeek.TUESDAY,
                                LocalTime.of(16, 0),
                                LocalTime.of(17, 30),
                                null
                        )
                ))
                .build());

        result.add(Subject.builder()
                .category("세계시민역량")
                .id("539250")
                .classNumber(4)
                .name("대학영어1([중급]문과대학)")
                .credit(3)
                .professor("나탈리할레만스")
                .times(List.of(
                        new Subject.TimeAndPlace(
                                DayOfWeek.TUESDAY,
                                LocalTime.of(16, 0),
                                LocalTime.of(17, 30),
                                "상경507"
                        ),
                        new Subject.TimeAndPlace(
                                DayOfWeek.THURSDAY,
                                LocalTime.of(16, 0),
                                LocalTime.of(17, 30),
                                "상경507"
                        )
                ))
                .build());

        result.add(Subject.builder()
                .category("세계시민역량")
                .id("539250")
                .classNumber(5)
                .name("대학영어1([중급]문과대학)")
                .credit(3)
                .professor("김언조")
                .times(List.of(
                        new Subject.TimeAndPlace(
                                DayOfWeek.TUESDAY,
                                LocalTime.of(16, 0),
                                LocalTime.of(17, 30),
                                "인문102"
                        ),
                        new Subject.TimeAndPlace(
                                DayOfWeek.THURSDAY,
                                LocalTime.of(16, 0),
                                LocalTime.of(17, 30),
                                "인문102"
                        )
                ))
                .build());
        return result;
    }

    private static List<MajorSubject> dummyMajorSubject() {
        List<MajorSubject> result = new ArrayList<>();

        result.add(new MajorSubject(
                "문과 국어국문학과",
                1,
                Subject.builder()
                        .category("학과기초")
                        .id("471580")
                        .classNumber(1)
                        .name("고전문학강독")
                        .credit(3)
                        .professor("권진옥")
                        .times(List.of(
                                new Subject.TimeAndPlace(
                                        DayOfWeek.TUESDAY,
                                        LocalTime.of(10, 30),
                                        LocalTime.of(12, 0),
                                        null
                                )
                        ))
                        .build()));

        result.add(new MajorSubject(
                "문과 국어국문학과",
                1,
                Subject.builder()
                        .category("학과기초")
                        .id("471580")
                        .classNumber(2)
                        .name("고전문학강독")
                        .credit(3)
                        .professor("맹영일")
                        .times(List.of(
                                new Subject.TimeAndPlace(
                                        DayOfWeek.TUESDAY,
                                        LocalTime.of(10, 30),
                                        LocalTime.of(12, 0),
                                        "인문403"
                                ),
                                new Subject.TimeAndPlace(
                                        DayOfWeek.THURSDAY,
                                        LocalTime.of(13, 30),
                                        LocalTime.of(15, 0),
                                        "인문408"
                                )
                        ))
                        .build()));

        result.add(new MajorSubject(
                "문과 철학과",
                2,
                Subject.builder()
                        .category("전공선택")
                        .id("345920")
                        .classNumber(1)
                        .name("미학")
                        .credit(3)
                        .professor("손지민")
                        .times(List.of(
                                new Subject.TimeAndPlace(
                                        DayOfWeek.WEDNESDAY,
                                        LocalTime.of(11, 0),
                                        LocalTime.of(12, 30),
                                        "인문323"
                                ),
                                new Subject.TimeAndPlace(
                                        DayOfWeek.THURSDAY,
                                        LocalTime.of(15, 0),
                                        LocalTime.of(16, 30),
                                        "인문323"
                                )
                        ))
                        .build()));

        result.add(new MajorSubject(
                "사회 커뮤니케이션학부 영상콘텐츠",
                2,
                Subject.builder()
                        .category("전공필수")
                        .id("338800")
                        .classNumber(1)
                        .name("매스컴이론")
                        .credit(3)
                        .professor("김지원")
                        .times(List.of(
                                new Subject.TimeAndPlace(
                                        DayOfWeek.MONDAY,
                                        LocalTime.of(11, 0),
                                        LocalTime.of(12, 30),
                                        "미디어102"
                                ),
                                new Subject.TimeAndPlace(
                                        DayOfWeek.WEDNESDAY,
                                        LocalTime.of(11, 0),
                                        LocalTime.of(12, 30),
                                        "체육125(주경기장)"
                                )
                        ))
                        .build()));

        return result;
    }
}