package com.dku.council.mock;

import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;

import java.util.ArrayList;
import java.util.List;

public class BusArrivalMock {

    public static BusArrival create() {
        return create("24");
    }

    public static BusArrival create(String busNo) {
        return new BusArrival(BusStatus.RUN,
                5, 10, "경기16바5555",
                7, 15, "경기16바1111",
                busNo);
    }

    public static List<BusArrival> createList(int size) {
        List<BusArrival> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(create());
        }
        return result;
    }
}
