package com.dku.council.domain.bus.model.dto;

import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import lombok.Getter;

@Getter
public class BusArrivalDto {
    private final BusStatus status;
    private final Integer locationNo1;
    private final Integer predictTime1;
    private final String plateNo1;
    private final Integer locationNo2;
    private final Integer predictTime2;
    private final String plateNo2;
    private final String busNo;

    public BusArrivalDto(BusArrival arrival) {
        this.status = arrival.getStatus();
        this.locationNo1 = arrival.getLocationNo1();
        this.predictTime1 = arrival.getPredictTimeSec1();
        this.plateNo1 = arrival.getPlateNo1();
        this.locationNo2 = arrival.getLocationNo2();
        this.predictTime2 = arrival.getPredictTimeSec2();
        this.plateNo2 = arrival.getPlateNo2();
        this.busNo = arrival.getBusNo();
    }
}