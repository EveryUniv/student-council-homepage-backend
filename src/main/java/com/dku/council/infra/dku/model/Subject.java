package com.dku.council.infra.dku.model;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Builder
@ToString
public class Subject {
    /**
     * 이수구분
     */
    private final String category;

    /**
     * 교과목번호
     */
    private final String id;

    /**
     * 분반
     */
    private final int classNumber;

    /**
     * 교과목명
     */
    private final String name;

    /**
     * 학점
     */
    private final int credit;

    /**
     * 교강사
     */
    private final String professor;

    /**
     * 시간
     */
    private final List<TimeAndPlace> times;

    protected Subject(Subject copy) {
        this.category = copy.category;
        this.id = copy.id;
        this.classNumber = copy.classNumber;
        this.name = copy.name;
        this.credit = copy.credit;
        this.professor = copy.professor;
        this.times = copy.times;
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class TimeAndPlace {
        private final DayOfWeek dayOfWeek;
        private final LocalTime from;
        private final LocalTime to;
        private final String place;
    }
}
