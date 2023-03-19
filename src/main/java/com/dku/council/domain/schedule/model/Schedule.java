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

    private LocalDate startDate;

    private LocalDate endDate;

    private String title;

    @Builder
    private Schedule(String title, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
