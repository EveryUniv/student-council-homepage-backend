package com.dku.council.domain.statistic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class PetitionStatisticDto {

    @Schema(description = "단과대", example = "공과대학")
    private final String department;

    @Schema(description = "동의 인원", example = "11")
    private final Integer agreeCount;
}
