package com.dku.council.domain.schedule.service;

import com.dku.council.domain.schedule.model.ScheduleDto;
import com.dku.council.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public List<ScheduleDto> getSchedules(LocalDate from, LocalDate to) {
        return scheduleRepository.findAllOverlapped(from, to).stream()
                .map(ScheduleDto::new)
                .collect(Collectors.toList());
    }
}
