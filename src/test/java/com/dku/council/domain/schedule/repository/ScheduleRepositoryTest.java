package com.dku.council.domain.schedule.repository;

import com.dku.council.domain.mainpage.model.entity.Schedule;
import com.dku.council.domain.mainpage.repository.ScheduleRepository;
import com.dku.council.mock.ScheduleMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        List<Schedule> list = ScheduleMock.createList(10,
                LocalDate.of(2022, 2, 10),
                Period.ofDays(2));
        scheduleRepository.saveAll(list);
    }

    @Test
    @DisplayName("일정을 시작일과 종료일로 조회한다.")
    void findAllOverlapped() {
        // when
        List<Schedule> list1 = scheduleRepository.findAllOverlapped(
                LocalDate.of(2022, 2, 1),
                LocalDate.of(2022, 2, 12));
        List<Schedule> list2 = scheduleRepository.findAllOverlapped(
                LocalDate.of(2022, 2, 5),
                LocalDate.of(2022, 2, 13));
        List<Schedule> list3 = scheduleRepository.findAllOverlapped(
                LocalDate.of(2022, 2, 13),
                LocalDate.of(2022, 2, 16));
        List<Schedule> list4 = scheduleRepository.findAllOverlapped(
                LocalDate.of(2022, 2, 16),
                LocalDate.of(2022, 3, 8));
        List<Schedule> list5 = scheduleRepository.findAllOverlapped(
                LocalDate.of(2022, 3, 19),
                LocalDate.of(2022, 3, 28));
        List<Schedule> list6 = scheduleRepository.findAllOverlapped(
                LocalDate.of(2022, 2, 26),
                LocalDate.of(2022, 2, 28));

        // then
        assertThat(list1).hasSize(1);
        assertThat(list2).hasSize(1);
        assertThat(list3).hasSize(2);
        assertThat(list4).hasSize(6);
        assertThat(list5).hasSize(1);
        assertThat(list6).hasSize(2);
    }

    @Test
    @DisplayName("일정을 시작일과 종료일로 삭제한다.")
    void deleteAllOverlapped() {
        // when
        scheduleRepository.deleteAllOverlapped(
                LocalDate.of(2022, 2, 16),
                LocalDate.of(2022, 3, 8));

        // then
        assertThat(scheduleRepository.count()).isEqualTo(4);
    }
}