package com.dku.council.domain.timetable.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TimeTableRequestDto {

    @NotBlank
    private final String name;

    @NotNull
    private final List<LectureDto> lectures;
}
