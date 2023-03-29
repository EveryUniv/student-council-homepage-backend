package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class LectureTemplateDto {

    @Schema(description = "아이디", example = "2")
    private Long id;

    @Schema(description = "수업 코드", example = "569124")
    private String lectureCode;

    @Schema(description = "이수구분", example = "세계시민역량")
    private String category;

    @Schema(description = "수업명", example = "대학영어1([중급]문과대학)")
    private String name;

    @Schema(description = "교강사", example = "찰스코퍼랜드")
    private String professor;

    @Schema(description = "분반", example = "3")
    private Integer classNumber;

    @Schema(description = "학점", example = "3")
    private Integer credit;

    @Schema(description = "수업 시간 및 장소")
    private final List<TimePromise> times;


    public LectureTemplateDto(ObjectMapper mapper, LectureTemplate template) {
        this.id = template.getId();
        this.lectureCode = template.getLectureId();
        this.category = template.getCategory();
        this.name = template.getName();
        this.professor = template.getProfessor();
        this.classNumber = template.getClassNumber();
        this.credit = template.getCredit();
        this.times = TimePromise.parse(mapper, template.getTimesJson());
    }
}
