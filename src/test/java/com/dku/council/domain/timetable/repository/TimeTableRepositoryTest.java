package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.LectureTime;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TimeTableRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeTableRepository repository;

    @Autowired
    private EntityManager em;

    private TimeTable timeTable;


    @BeforeEach
    void setup() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        User user = UserMock.create(major);
        user = userRepository.save(user);

        timeTable = new TimeTable(user, "test");

        Lecture lecture = Lecture.builder()
                .name("name")
                .place("place")
                .professor("professor")
                .build();
        lecture.changeTimeTable(timeTable);

        LectureTime lectureTime = LectureTime.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(13, 0))
                .week(DayOfWeek.FRIDAY)
                .build();
        lectureTime.changeLecture(lecture);

        timeTable = repository.save(timeTable);
    }

    @Test
    @DisplayName("Timetable을 persist시 모두 잘 저장되는지 확인")
    public void persistCascade() {
        // when
        em.flush();
        em.clear();

        // then
        TimeTable actualTable = repository.findById(timeTable.getId()).orElseThrow();
        assertThat(actualTable.getName()).isEqualTo("test");
        assertThat(actualTable.getLectures().size()).isEqualTo(1);

        Lecture actualLecture = actualTable.getLectures().get(0);
        assertThat(actualLecture.getName()).isEqualTo("name");
        assertThat(actualLecture.getPlace()).isEqualTo("place");
        assertThat(actualLecture.getProfessor()).isEqualTo("professor");
        assertThat(actualLecture.getLectureTimes().size()).isEqualTo(1);

        LectureTime actualLectureTime = actualLecture.getLectureTimes().get(0);
        assertThat(actualLectureTime.getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(actualLectureTime.getEndTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(actualLectureTime.getWeek()).isEqualTo(DayOfWeek.FRIDAY);
    }

    @Test
    @DisplayName("Timetable 삭제시 모두 잘 삭제되는지 확인")
    public void deleteCascade() {
        // given
        Lecture actualLecture = timeTable.getLectures().get(0);
        LectureTime actualLectureTime = actualLecture.getLectureTimes().get(0);

        // when
        repository.delete(timeTable);

        em.flush();
        em.clear();

        // then
        assertThat(repository.findById(timeTable.getId())).isEmpty();
        assertThat(em.find(Lecture.class, actualLecture.getId())).isNull();
        assertThat(em.find(LectureTime.class, actualLectureTime.getId())).isNull();
    }
}