package com.dku.council.infra.bus.provider;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShuttleBusProvider implements BusArrivalProvider {

    @Override
    public List<BusArrival> retrieveBusArrival(BusStation station) {
        return List.of();
    }

    @Override
    public String getProviderPrefix() {
        return "DKU_";
    }
}
