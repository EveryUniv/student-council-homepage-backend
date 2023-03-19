package com.dku.council.infra.dku.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class Schedule {
    private final String title;
    private final LocalDate fromDate;
    private final LocalDate toDate;
}