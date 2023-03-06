package com.dku.council.domain.bus.model;

import com.dku.council.domain.bus.model.dto.ResponseOpenApiBusArrival;
import lombok.Getter;

@Getter
public class BusArrival {
    private final String flag;
    private final Integer locationNo1;
    private final Integer predictTime1;
    private final String plateNo1;
    private final Integer locationNo2;
    private final Integer predictTime2;
    private final String plateNo2;
    private final String busNo;

    public BusArrival(ResponseOpenApiBusArrival.Body.BusArrival modelArrival) {
        this.flag = modelArrival.getFlag();
        this.locationNo1 = modelArrival.getLocationNo1();
        this.predictTime1 = modelArrival.getPredictTime1();
        this.plateNo1 = modelArrival.getPlateNo1();
        this.locationNo2 = modelArrival.getLocationNo2();
        this.predictTime2 = modelArrival.getPredictTime2();
        this.plateNo2 = modelArrival.getPlateNo2();

        String routeId = modelArrival.getRouteId();
        Bus bus = Bus.of(routeId);
        if (bus == null) {
            throw new IllegalArgumentException("Can't not find bus: " + routeId);
        }

        this.busNo = bus.getName();
    }
}