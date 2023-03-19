package com.dku.council.domain.schedule.service;

import com.dku.council.domain.schedule.model.Schedule;
import com.dku.council.domain.schedule.model.ScheduleDto;
import com.dku.council.domain.schedule.repository.ScheduleRepository;
import com.dku.council.mock.ScheduleMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository repository;

    @InjectMocks
    private ScheduleService service;

    @Test
    @DisplayName("일정 조회")
    void getSchedules() {
        // given
        LocalDate begin = LocalDate.of(2022, 3, 1);
        LocalDate end = LocalDate.of(2022, 3, 31);
        List<Schedule> schedules = ScheduleMock.createList(5, begin, Period.ofDays(2));
        Mockito.when(repository.findAllOverlapped(begin, end))
                .thenReturn(schedules);

        // when
        List<ScheduleDto> expected = service.getSchedules(begin, end);

        // then
        assertThat(expected).hasSize(5);
        for (int i = 0; i < 5; i++) {
            assertThat(expected.get(i).getTitle()).isEqualTo(schedules.get(i).getTitle());
            assertThat(expected.get(i).getStart()).isEqualTo(schedules.get(i).getStartDate());
            assertThat(expected.get(i).getEnd()).isEqualTo(schedules.get(i).getEndDate());
        }
    }
}