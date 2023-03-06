package com.dku.council.infra.bus.service.api;

import com.dku.council.infra.bus.model.BusArrival;

import java.util.List;

public interface BusArrivalInfoService {
    List<BusArrival> retrieveBusArrival(String stationId);
}
