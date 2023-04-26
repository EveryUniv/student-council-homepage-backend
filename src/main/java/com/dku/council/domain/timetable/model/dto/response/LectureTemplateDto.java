package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class LectureTemplateDto {

    @Schema(description = "학과 (전공만)", example = "SW융합 소프트웨어학과")
    private final String major;

    @Schema(description = "학년 (전공만)", example = "3")
    private final Integer grade;

    @Schema(description = "수업 코드", example = "569124")
    private final String lectureCode;

    @Schema(description = "이수구분", example = "세계시민역량")
    private final String category;

    @Schema(description = "수업명", example = "대학영어1([중급]문과대학)")
    private final String name;

    @Schema(description = "교강사", example = "찰스코퍼랜드")
    private final String professor;

    @Schema(description = "분반", example = "3")
    private final Integer classNumber;

    @Schema(description = "학점", example = "3")
    private final Integer credit;

    @Schema(description = "수업 시간 및 장소")
    private final List<TimePromise> times;


    public LectureTemplateDto(ObjectMapper mapper, LectureTemplate template) {
        this.lectureCode = template.getLectureId();
        this.category = template.getCategory();
        this.name = template.getName();
        this.professor = template.getProfessor();
        this.classNumber = template.getClassNumber();
        this.credit = template.getCredit();
        this.times = TimePromise.parse(mapper, template.getTimesJson());

        if (template.getMajor() != null) {
            this.major = template.getMajor();
            this.grade = template.getGrade();
        } else {
            this.major = null;
            this.grade = null;
        }
    }
}
