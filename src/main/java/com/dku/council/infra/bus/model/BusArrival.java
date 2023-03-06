package com.dku.council.infra.bus.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class BusArrival {
    private final BusStatus status;
    private final Integer locationNo1;
    private final Integer predictTime1;
    private final String plateNo1;
    private final Integer locationNo2;
    private final Integer predictTime2;
    private final String plateNo2;
    private final String busNo;
}