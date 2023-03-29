package com.dku.council.mock;

import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.LectureTime;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LectureMock {
    public static List<Lecture> createLectureList(int size) {
        List<Lecture> lectures = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Lecture lecture = Lecture.builder()
                    .name("lecture" + i)
                    .professor("professor" + i)
                    .build();
            lectures.add(lecture);
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
