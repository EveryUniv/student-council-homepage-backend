package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.domain.timetable.model.entity.TimeSchedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class LectureDto {

    private final Long id;
    private final String name;
    private final String professor;
    private final List<TimePromise> times;


    public LectureDto(ObjectMapper mapper, TimeSchedule lecture) {
        this.id = lecture.getId();
        this.name = lecture.getName();
        this.professor = lecture.getMemo();
        this.times = TimePromise.parse(mapper, lecture.getTimesJson());
    }
}
