package com.dku.council.domain.mainpage.service;

import com.dku.council.domain.mainpage.model.dto.response.ScheduleResponseDto;
import com.dku.council.domain.mainpage.model.entity.Schedule;
import com.dku.council.domain.mainpage.repository.ScheduleRepository;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.ScheduleInfo;
import com.dku.council.infra.dku.service.DkuAuthenticationService;
import com.dku.council.infra.dku.service.DkuScheduleService;
import com.dku.council.mock.ScheduleMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DkuAuthenticationService dkuAuthenticationService;

    @Mock
    private DkuScheduleService dkuScheduleService;

    private ScheduleService service;

    @BeforeEach
    void setUp() {
        service = new ScheduleService(scheduleRepository, dkuAuthenticationService, dkuScheduleService,
                "id", "password", Period.ofMonths(6));
    }

    @Test
    @DisplayName("일정을 조회한다.")
    void getSchedules() {
        // given
        LocalDate from = LocalDate.of(2022, 3, 5);
        LocalDate to = LocalDate.of(2022, 11, 5);
        List<Schedule> schedules = ScheduleMock.createList(5, from, Period.ofDays(5));
        when(scheduleRepository.findAllOverlapped(from, to)).thenReturn(schedules);

        // when
        List<ScheduleResponseDto> actual = service.getSchedules(from, to);

        // then
        for (int i = 0; i < 5; i++) {
            ScheduleResponseDto dto = actual.get(i);
            Schedule schedule = schedules.get(i);
            assertThat(schedule.getTitle()).isEqualTo(dto.getTitle());
            assertThat(schedule.getStartDate()).isEqualTo(dto.getStart());
            assertThat(schedule.getEndDate()).isEqualTo(dto.getEnd());
        }
    }

    @Test
    @DisplayName("일정을 DB에 불러온다.")
    void loadSchedulesYear() {
        // given
        LocalDate date = LocalDate.of(2023, 5, 7);
        LocalDate start = LocalDate.of(2022, 11, 1);
        LocalDate end = LocalDate.of(2023, 11, 30);
        DkuAuth auth = new DkuAuth(new LinkedMultiValueMap<>());
        List<ScheduleInfo> scheduleInfos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            scheduleInfos.add(new ScheduleInfo("title" + i, start.plusDays(i * 2), end.plusDays(i * 2 + 1)));
        }

        when(dkuAuthenticationService.loginPortal("id", "password")).thenReturn(auth);
        when(dkuScheduleService.crawlSchedule(auth, start, end)).thenReturn(scheduleInfos);

        // when
        service.loadSchedulesYear(date);

        // then
        verify(scheduleRepository).deleteAllOverlapped(start, end);
        verify(scheduleRepository).saveAll(argThat(schedules -> {
            for (int i = 0; i < 10; i++) {
                Schedule schedule = ((List<Schedule>) schedules).get(i);
                ScheduleInfo scheduleInfo = scheduleInfos.get(i);
                assertThat(schedule.getTitle()).isEqualTo(scheduleInfo.getTitle());
                assertThat(schedule.getStartDate()).isEqualTo(scheduleInfo.getFromDate());
                assertThat(schedule.getEndDate()).isEqualTo(scheduleInfo.getToDate());
            }
            return true;
        }));
    }
}