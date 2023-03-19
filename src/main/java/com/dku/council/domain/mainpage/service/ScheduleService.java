package com.dku.council.domain.mainpage.service;

import com.dku.council.domain.mainpage.model.dto.response.ScheduleResponseDto;
import com.dku.council.domain.mainpage.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public List<ScheduleResponseDto> getSchedules(LocalDate from, LocalDate to) {
        return scheduleRepository.findAllOverlapped(from, to).stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }
}
