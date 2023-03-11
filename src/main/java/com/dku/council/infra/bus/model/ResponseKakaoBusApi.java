package com.dku.council.infra.bus.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ResponseKakaoBusApi {
    private final List<BusLine> lines;
    private final String name;

    @Getter
    @RequiredArgsConstructor
    public static class BusLine {
        private final String name;
        private final boolean running;
        private final BusArrival arrival;

        @Getter
        @RequiredArgsConstructor
        public static class BusArrival {
            private final int arrivalTime;
            private final int busStopCount;
            private final String vehicleNumber;
            private final String vehicleState;
            private final String nextBusStopName;
            private final int arrivalTime2;
            private final int busStopCount2;
            private final String vehicleNumber2;
            private final String vehicleState2;
            private final int order;
        }
    }
}
