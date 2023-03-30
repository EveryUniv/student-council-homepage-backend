package com.dku.council.domain.timetable.model.mapper;

import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import com.dku.council.domain.timetable.model.entity.TimeSchedule;

public class TimeScheduleMapper {
    public static TimeSchedule createScheduleFromLecture(LectureTemplate lecture, String color) {
        return TimeSchedule.builder()
                .name(lecture.getName())
                .memo(lecture.getProfessor())
                .color(color)
                .timesJson(lecture.getTimesJson())
                .build();
    }
}
