package com.dku.council.domain.timetable.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UpdateTimeTableRequestDto {

    @NotNull
    private final List<RequestScheduleDto> lectures;
}
