package com.dku.council.domain.timetable.model.entity;

import com.dku.council.domain.timetable.model.TimeScheduleType;
import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TimeSchedule extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "timetable_id")
    private TimeTable timetable;

    private String name;

    private String memo;

    private TimeScheduleType type;

    private String color;

    private String timesJson;


    @Builder
    private TimeSchedule(String name, String memo, TimeScheduleType type, String color, String timesJson) {
        this.name = name;
        this.memo = memo;
        this.type = type;
        this.color = color;
        this.timesJson = timesJson;
    }


    public void changeTimeTable(TimeTable timetable) {
        if (this.timetable != null) {
            this.timetable.getSchedules().remove(this);
        }

        this.timetable = timetable;
        timetable.getSchedules().add(this);
    }

}
