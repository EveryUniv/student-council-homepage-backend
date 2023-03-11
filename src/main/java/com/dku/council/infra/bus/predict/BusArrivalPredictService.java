package com.dku.council.infra.bus.predict;

import com.dku.council.domain.bus.model.BusStation;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;

public interface BusArrivalPredictService {
    @Nullable
    Duration remainingNextBusArrival(String busNo, BusStation station, LocalDateTime now);
}
