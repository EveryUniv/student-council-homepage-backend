package com.dku.council.domain.timetable.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class LectureTime extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "lecture_seq_generator"
    )
    @Column(name = "lectime_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private LocalTime startTime;

    private LocalTime endTime;

    private String place;

    @Enumerated(STRING)
    private DayOfWeek week;


    @Builder
    private LectureTime(LocalTime startTime, LocalTime endTime, DayOfWeek week, String place) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.week = week;
        this.place = place;
    }

    public void changeLecture(Lecture lecture) {
        if (this.lecture != null) {
            this.lecture.getLectureTimes().remove(this);
        }

        this.lecture = lecture;
        this.lecture.getLectureTimes().add(this);
    }
}
