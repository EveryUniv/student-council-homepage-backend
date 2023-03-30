package com.dku.council.domain.mainpage.service;

import com.dku.council.domain.mainpage.model.dto.response.ScheduleResponseDto;
import com.dku.council.domain.mainpage.model.entity.Schedule;
import com.dku.council.domain.mainpage.repository.ScheduleRepository;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.ScheduleInfo;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DkuAuthenticationService dkuAuthenticationService;
    private final DkuScheduleService dkuScheduleService;

    @Value("${dku.static-crawler.id}")
    private final String id;

    @Value("${dku.static-crawler.password}")
    private final String password;

    @Value("${dku.schedule.half-range}")
    private final Period halfRange;


    public List<ScheduleResponseDto> getSchedules(LocalDate from, LocalDate to) {
        return scheduleRepository.findAllOverlapped(from, to).stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void loadSchedulesYear(LocalDate now) {
        LocalDate start = YearMonth.from(now.minus(halfRange)).atDay(1);
        LocalDate end = YearMonth.from(now.plus(halfRange)).atEndOfMonth();

        DkuAuth auth = dkuAuthenticationService.loginPortal(id, password);
        List<ScheduleInfo> schedules = dkuScheduleService.crawlSchedule(auth, start, end);

        List<Schedule> entities = schedules.stream()
                .map(e -> Schedule.builder()
                        .title(e.getTitle())
                        .startDate(e.getFromDate())
                        .endDate(e.getToDate())
                        .build())
                .collect(Collectors.toList());

        scheduleRepository.deleteAllOverlapped(start, end);
        scheduleRepository.saveAll(entities);
    }
}
