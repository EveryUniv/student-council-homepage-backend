package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.dto.response.TimePromise;
import com.dku.council.domain.timetable.model.entity.TimeSchedule;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.ObjectMapperGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

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
    private TimeScheduleRepository timeScheduleRepository;

    @Autowired
    private EntityManager em;

    private final ObjectMapper mapper = ObjectMapperGenerator.create();
    private TimeTable timeTable;


    @BeforeEach
    void setup() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        User user = UserMock.create(major);
        user = userRepository.save(user);

        timeTable = new TimeTable(user, "test");

        List<TimePromise> lectureTime = List.of(
                new TimePromise(LocalTime.of(10, 0), LocalTime.of(13, 0),
                        DayOfWeek.FRIDAY, "place")
        );

        TimeSchedule lecture = TimeSchedule.builder()
                .name("name")
                .memo("professor")
                .color("color")
                .timesJson(TimePromise.serialize(mapper, lectureTime))
                .build();
        timeScheduleRepository.save(lecture);
        timeTable = repository.save(timeTable);
    }

    @Test
    @DisplayName("Timetable 삭제시 schedule 잘 삭제되는지 확인")
    public void deleteCascade() {
        // given
        TimeSchedule actualLecture = timeTable.getSchedules().get(0);

        // when
        repository.delete(timeTable);

        em.flush();
        em.clear();

        // then
        assertThat(repository.findById(timeTable.getId())).isEmpty();
        assertThat(em.find(TimeSchedule.class, actualLecture.getId())).isNull();
    }

    @Test
    @DisplayName("Timetable schedules 리스트에서 삭제시 schedule 잘 삭제되는지 확인")
    public void deleteInList() {
        // given
        TimeSchedule actualLecture = timeTable.getSchedules().get(0);

        // when
        timeTable.getSchedules().clear();

        em.flush();
        em.clear();

        // then
        assertThat(em.find(TimeSchedule.class, actualLecture.getId())).isNull();
    }
}