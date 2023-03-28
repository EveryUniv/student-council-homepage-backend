package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.TimeTableLecture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class LectureDto {

    private final String name;
    private final String professor;
    private final String color;
    private final List<LectureTimeDto> times;


    public LectureDto(TimeTableLecture mapping) {
        Lecture lec = mapping.getLecture();
        this.name = lec.getName();
        this.professor = lec.getProfessor();
        this.color = mapping.getColor();
        this.times = lec.getLectureTimes().stream()
                .map(LectureTimeDto::new)
                .collect(Collectors.toList());
    }
}
