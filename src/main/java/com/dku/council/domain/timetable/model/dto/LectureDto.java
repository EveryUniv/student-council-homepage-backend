package com.dku.council.domain.timetable.model.dto;

import com.dku.council.domain.timetable.model.entity.Lecture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class LectureDto {

    private final String name;
    private final String professor;
    private final String place;
    private final List<LectureTimeDto> times;


    public LectureDto(Lecture lecture) {
        this.name = lecture.getName();
        this.professor = lecture.getProfessor();
        this.place = lecture.getPlace();
        this.times = lecture.getLectureTimes().stream()
                .map(LectureTimeDto::new)
                .collect(Collectors.toList());
    }
}
