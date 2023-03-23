package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.model.dto.LectureDto;
import com.dku.council.domain.timetable.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;

    public List<LectureDto> list(String name) {
        return null;
    }
}
