package com.dku.council.domain.timetable.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SequenceGenerator(
        name = "lecture_template_seq_generator",
        allocationSize = 300
)
@Table(
        indexes = {
                @Index(name = "idx_lecture_template_name", columnList = "name")
        }
)
public class LectureTemplate extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "lecture_template_seq_generator"
    )
    @Column(name = "lec_id")
    private Long id;

    private String major;

    private int grade;

    private String lectureId;

    private String category;

    private String name;

    private String professor;

    private Integer classNumber;

    private Integer credit;

    private String timesJson;


    @Builder
    private LectureTemplate(String major, int grade, String lectureId, String category, String name,
                            String professor, Integer classNumber, Integer credit, String timesJson) {
        this.major = major;
        this.grade = grade;
        this.lectureId = lectureId;
        this.category = category;
        this.name = name;
        this.professor = professor;
        this.classNumber = classNumber;
        this.credit = credit;
        this.timesJson = timesJson;
    }
}
