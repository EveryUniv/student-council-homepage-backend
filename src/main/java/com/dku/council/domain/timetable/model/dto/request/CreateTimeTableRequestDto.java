package com.dku.council.domain.timetable.model.dto.request;

import com.dku.council.domain.timetable.model.dto.response.LectureDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateTimeTableRequestDto {

    @NotBlank
    private final String name;

    @NotNull
    private final List<LectureDto> lectures;
}
