package com.dku.council.domain.schedule.repository;

import com.dku.council.domain.schedule.model.Schedule;
import com.dku.council.mock.ScheduleMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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
    void findAllOverlapped() {
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

        Assertions.assertThat(list1).hasSize(1);
        Assertions.assertThat(list2).hasSize(1);
        Assertions.assertThat(list3).hasSize(2);
        Assertions.assertThat(list4).hasSize(6);
        Assertions.assertThat(list5).hasSize(1);
        Assertions.assertThat(list6).hasSize(2);
    }
}