package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.entity.LectureTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class LectureTimeDto {

    @Schema(description = "시작 시각", example = "09:00:00")
    private final LocalTime start;

    @Schema(description = "종료 시각", example = "13:00:00")
    private final LocalTime end;

    @Schema(description = "요일")
    private final DayOfWeek week;

    @Schema(description = "수업 장소")
    private final String place;

    public LectureTimeDto(LectureTime lectureTime) {
        this.start = lectureTime.getStartTime();
        this.end = lectureTime.getEndTime();
        this.week = lectureTime.getWeek();
        this.place = lectureTime.getPlace();
    }
}
