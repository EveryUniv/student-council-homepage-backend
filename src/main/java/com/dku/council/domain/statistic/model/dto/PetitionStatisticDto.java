package com.dku.council.domain.statistic.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class PetitionStatisticDto {
    private final List<Map.Entry<String, Integer>> top4Department;
}
