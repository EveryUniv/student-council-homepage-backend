package com.dku.council.domain.timetable.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TimeTableLecture extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "mapping_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private TimeTable timetable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private String color;


    public TimeTableLecture(Lecture lecture, String color) {
        this.lecture = lecture;
        this.color = color;
    }

    public void changeTimeTable(TimeTable timetable) {
        if (this.timetable != null) {
            this.timetable.getLectures().remove(this);
        }

        this.timetable = timetable;
        this.timetable.getLectures().add(this);
    }
}
