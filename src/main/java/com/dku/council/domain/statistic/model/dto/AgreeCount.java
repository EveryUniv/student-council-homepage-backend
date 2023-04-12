package com.dku.council.domain.statistic.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AgreeCount {
    private final String department;
    private final Long count;
}
