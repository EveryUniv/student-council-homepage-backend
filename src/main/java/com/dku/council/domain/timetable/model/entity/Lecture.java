package com.dku.council.domain.timetable.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Lecture extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "timetable_id")
    private TimeTable timetable;

    private String name;

    private String professor;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<LectureTime> lectureTimes = new ArrayList<>();


    @Builder
    private Lecture(String name, String professor) {
        this.name = name;
        this.professor = professor;
    }
}
