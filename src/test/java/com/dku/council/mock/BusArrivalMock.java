package com.dku.council.mock;

import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;

import java.util.ArrayList;
import java.util.List;

public class BusArrivalMock {

    public static final int PREDICT_TIME_SEC1 = 30;
    public static final int PREDICT_TIME_SEC2 = 60;

    public static BusArrival create() {
        return create("24");
    }

    public static BusArrival create(int predict1, int predict2) {
        return create("24", 0, predict1, predict2, BusStatus.RUN);
    }

    public static BusArrival create(String busNo) {
        return create(busNo, 0);
    }

    public static BusArrival create(String busNo, int stationOrder) {
        return create(busNo, stationOrder, BusStatus.RUN);
    }

    public static BusArrival create(String busNo, BusStatus status) {
        return create(busNo, 0, status);
    }

    public static BusArrival create(String busNo, int stationOrder, BusStatus status) {
        return create(busNo, stationOrder, PREDICT_TIME_SEC1, PREDICT_TIME_SEC2, status);
    }

    public static BusArrival create(String busNo, int stationOrder, int predict1, int predict2, BusStatus status) {
        return new BusArrival(status, stationOrder,
                5, predict1, "경기16바5555",
                7, predict2, "경기16바1111",
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
