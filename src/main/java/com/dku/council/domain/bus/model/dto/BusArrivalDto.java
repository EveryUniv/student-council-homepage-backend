package com.dku.council.domain.bus.model.dto;

import com.dku.council.domain.bus.model.BusArrival;
import lombok.Getter;

@Getter
public class BusArrivalDto {
    private final String flag;
    private final Integer locationNo1;
    private final Integer predictTime1;
    private final String plateNo1;
    private final Integer locationNo2;
    private final Integer predictTime2;
    private final String plateNo2;
    private final String busNo;

    public BusArrivalDto(BusArrival arrival) {
        this.flag = arrival.getFlag();
        this.locationNo1 = arrival.getLocationNo1();
        this.predictTime1 = arrival.getPredictTime1();
        this.plateNo1 = arrival.getPlateNo1();
        this.locationNo2 = arrival.getLocationNo2();
        this.predictTime2 = arrival.getPredictTime2();
        this.plateNo2 = arrival.getPlateNo2();
        this.busNo = arrival.getBusNo();
    }
}