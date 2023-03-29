package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.entity.Lecture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class LectureDto {

    private final Long id;
    private final String name;
    private final String professor;
    private final List<LectureTimeDto> times;


    public LectureDto(Lecture lecture) {
        this.id = lecture.getId();
        this.name = lecture.getName();
        this.professor = lecture.getProfessor();
        this.times = lecture.getLectureTimes().stream()
                .map(LectureTimeDto::new)
                .collect(Collectors.toList());
    }
}
