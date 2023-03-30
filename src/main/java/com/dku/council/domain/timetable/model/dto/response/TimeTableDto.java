package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class TimeTableDto {

    @Schema(description = "아이디", example = "2")
    private final Long id;

    @Schema(description = "시간표 이름", example = "1학기 시간표")
    private final String name;

    @Schema(description = "일정 목록")
    private final List<TimeScheduleDto> lectures;


    public TimeTableDto(ObjectMapper mapper, TimeTable timeTable) {
        this.id = timeTable.getId();
        this.name = timeTable.getName();
        this.lectures = timeTable.getSchedules().stream()
                .map(e -> new TimeScheduleDto(mapper, e))
                .collect(Collectors.toList());
    }
}

