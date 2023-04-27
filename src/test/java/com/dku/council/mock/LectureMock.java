package com.dku.council.mock;

import com.dku.council.domain.timetable.model.TimeScheduleType;
import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import com.dku.council.domain.timetable.model.entity.TimeSchedule;
import com.dku.council.util.EntityUtil;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LectureMock {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static List<TimeSchedule> createLectureList() {
        return createList(4, false, LectureMock::lecture);
    }

    public static List<TimeSchedule> createOverlappedLectureList() {
        List<TimeSchedule> lectures = new ArrayList<>();
        lectures.add(lecture(0, LocalTime.of(16, 0), LocalTime.of(17, 40)));
        lectures.add(lecture(1, LocalTime.of(17, 30), LocalTime.of(18, 0)));
        lectures.add(lecture(2, LocalTime.of(17, 45), LocalTime.of(18, 30)));
        lectures.add(lecture(3, LocalTime.of(18, 0), LocalTime.of(18, 10)));
        return lectures;
    }

    public static List<LectureTemplate> createLectureTemplateList() {
        return createList(4, false, LectureMock::lectureTemplate);
    }

    private static <T> List<T> createList(int size, boolean overlapped, EntityMapper<T> mapper) {
        Duration dura = Duration.of(1440 / size, ChronoUnit.MINUTES);
        LocalTime start = LocalTime.of(0, 0);

        List<T> lectures = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LocalTime first, end;

            if (overlapped) {
                first = LocalTime.of(16, 0);
                end = LocalTime.of(17, 30);
            } else {
                first = start;
                end = start = start.plus(dura);
            }

            lectures.add(mapper.map(i, first, end));
        }

        return lectures;
    }

    private static TimeSchedule lecture(int i, LocalTime start, LocalTime end) {
        TimeScheduleType[] values = TimeScheduleType.values();
        TimeSchedule e = TimeSchedule.builder()
                .name("lecture" + i)
                .timesJson(timesJson(start, end))
                .type(values[i % values.length])
                .memo("professor" + i)
                .color("color" + i)
                .build();
        EntityUtil.injectId(TimeSchedule.class, e, (long) i);
        return e;
    }

    private static LectureTemplate lectureTemplate(int i, LocalTime start, LocalTime end) {
        LectureTemplate e = LectureTemplate.builder()
                .category("세계시민역량")
                .lectureId("539250")
                .classNumber(1)
                .name("lecture" + i)
                .credit(3)
                .professor("professor" + i)
                .timesJson(timesJson(start, end))
                .build();
        EntityUtil.injectId(LectureTemplate.class, e, (long) i);
        return e;
    }

    private static String timesJson(LocalTime start, LocalTime end) {
        return String.format("[{\"week\":\"TUESDAY\",\"start\":\"%s\",\"end\":\"%s\",\"place\":\"place1\"}]",
                start.format(FORMATTER), end.format(FORMATTER)
        );
    }

    @FunctionalInterface
    private interface EntityMapper<T> {
        T map(int i, LocalTime start, LocalTime end);
    }
}
