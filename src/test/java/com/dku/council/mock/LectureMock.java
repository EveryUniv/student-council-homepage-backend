package com.dku.council.mock;

import com.dku.council.domain.timetable.model.entity.TimeSchedule;

import java.util.ArrayList;
import java.util.List;

public class LectureMock {
    public static List<TimeSchedule> createLectureList(int size) {
        List<TimeSchedule> lectures = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TimeSchedule lecture = TimeSchedule.builder()
                    .name("lecture" + i)
                    .timesJson("[{\"week\":\"TUESDAY\",\"start\":\"16:00:00\",\"end\":\"17:30:00\",\"place\":\"place1\"}" +
                            ",{\"week\":\"THURSDAY\",\"start\":\"12:00:00\",\"end\":\"15:30:00\",\"place\":\"place2\"}]")
                    .memo("professor" + i)
                    .color("color" + i)
                    .build();
            lectures.add(lecture);
        }
        return lectures;
    }
}
