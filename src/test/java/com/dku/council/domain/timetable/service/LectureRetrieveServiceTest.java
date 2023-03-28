package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.LectureTime;
import com.dku.council.domain.timetable.repository.LectureRepository;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.Subject;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuLectureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureRetrieveServiceTest {

    @Mock
    private DkuAuthenticationService dkuAuthenticationService;

    @Mock
    private DkuLectureService dkuLectureService;

    @Mock
    private LectureRepository lectureRepository;

    private LectureRetrieveService service;

    @BeforeEach
    void setUp() {
        service = new LectureRetrieveService(dkuAuthenticationService, dkuLectureService, lectureRepository,
                "id", "password");
    }

    @Test
    @DisplayName("대학 강의 목록을 DB로 가져온다.")
    void reloadLectures() {
        // given
        YearMonth yearMonth = YearMonth.of(2022, 9);
        DkuAuth auth = new DkuAuth(new LinkedMultiValueMap<>());
        List<Subject> subjects = createSubjects();

        when(dkuAuthenticationService.loginWebInfo("id", "password")).thenReturn(auth);
        when(dkuLectureService.crawlLecture(auth, yearMonth)).thenReturn(subjects);

        // when
        service.reloadLectures(yearMonth);

        // then
        verify(lectureRepository).deleteAll();
        verify(lectureRepository).saveAll(checkSubjects(subjects));
    }

    private static Iterable<Lecture> checkSubjects(List<Subject> subjects) {
        return argThat(lectures -> {
            for (int i = 0; i < subjects.size(); i++) {
                Lecture lecture = ((List<Lecture>) lectures).get(i);
                Subject subject = subjects.get(i);
                assertThat(lecture.getName()).isEqualTo(subject.getName());
                assertThat(lecture.getProfessor()).isEqualTo(subject.getProfessor());
                assertThat(lecture.getLectureTimes().size()).isEqualTo(subject.getTimes().size());

                LectureTime lectureTime = lecture.getLectureTimes().get(0);
                Subject.TimeAndPlace time = subject.getTimes().get(0);
                assertThat(lectureTime.getStartTime()).isEqualTo(time.getFrom());
                assertThat(lectureTime.getEndTime()).isEqualTo(time.getTo());
                assertThat(lectureTime.getPlace()).isEqualTo(time.getPlace());
                assertThat(lectureTime.getWeek()).isEqualTo(time.getDayOfWeek());

            }
            return true;
        });
    }

    private static List<Subject> createSubjects() {
        return List.of(
                Subject.builder()
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
                        .build(),
                Subject.builder()
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
                        .build()
        );
    }
}