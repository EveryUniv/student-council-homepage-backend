package com.dku.council.mock;

import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.LectureTime;
import com.dku.council.domain.timetable.model.entity.TimeTable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeTableMock {
    public static final String TABLE_NAME = "name";
    
    public static TimeTable createDummy() {
        TimeTable table = new TimeTable(UserMock.createDummyMajor(), TABLE_NAME);
        createLectureList(table, 5);
        return table;
    }

    public static List<Lecture> createLectureList(TimeTable table, int size) {
        List<Lecture> lectures = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Lecture lecture = Lecture.builder()
                    .name("lecture" + i)
                    .professor("professor" + i)
                    .place("place" + i)
                    .build();
            lectures.add(lecture);
            if (table != null) {
                lecture.changeTimeTable(table);
            }
            createLectureTimeList(lecture, 2);
        }
        return lectures;
    }

    public static List<LectureTime> createLectureTimeList(Lecture lecture, int size) {
        List<LectureTime> lectureTimes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LectureTime lectureTime = LectureTime.builder()
                    .week(DayOfWeek.of(i % 7 + 1))
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(13, 0))
                    .build();
            lectureTimes.add(lectureTime);
            if (lecture != null) {
                lectureTime.changeLecture(lecture);
            }
        }
        return lectureTimes;
    }
}
