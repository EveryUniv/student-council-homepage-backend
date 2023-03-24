package com.dku.council.domain.timetable.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class UpdateTimeTableNameRequestDto {

    @NotBlank
    private final String name;
}
