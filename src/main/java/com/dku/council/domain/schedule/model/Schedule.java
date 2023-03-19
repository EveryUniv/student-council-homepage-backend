package com.dku.council.domain.schedule.model;

import com.dku.council.global.base.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "schedule_id")
    private Long id;

    private LocalDate startDateTime;

    private LocalDate endDateTime;

    private String title;

    @Builder
    private Schedule(String title, LocalDate startDateTime, LocalDate endDateTime) {
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
