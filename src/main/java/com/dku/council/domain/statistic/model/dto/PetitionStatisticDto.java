package com.dku.council.domain.statistic.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class PetitionStatisticDto {
    private final String department;
    private final Integer agreeCount;
}
