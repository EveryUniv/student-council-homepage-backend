package com.dku.council.infra.bus.model.mapper;

import com.dku.council.domain.bus.model.Bus;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import com.dku.council.infra.bus.model.ResponseGGBusArrival;
import com.dku.council.infra.bus.model.ResponseKakaoBusApi;

public class BusResponseMapper {
    public static BusArrival to(ResponseGGBusArrival.Body.BusArrival model) {
        String routeId = model.getRouteId();
        Bus bus = Bus.of(routeId);
        if (bus == null) {
            return null;
        }

        BusStatus state = getGGBusStateLabel(model.getFlag());
        return new BusArrival(state, model.getStaOrder(),
                model.getLocationNo1(), model.getPredictTime1() * 60, model.getPlateNo1(),
                model.getLocationNo2(), model.getPredictTime2() * 60, model.getPlateNo2(),
                bus.getName());
    }

    private static BusStatus getGGBusStateLabel(String id) {
        switch (id) {
            case "STOP": // 운행종료
                return BusStatus.STOP;
            case "WAIT": // 회차지대기
                return BusStatus.WAITING;
            case "RUN": // 운행중
            case "PASS": // 운행중
                return BusStatus.RUN;
        }
        return BusStatus.RUN;
    }

    public static BusArrival to(ResponseKakaoBusApi.BusLine model) {
        ResponseKakaoBusApi.BusLine.BusArrival arrival = model.getArrival();
        BusStatus state = getKakaoStateLabel(arrival.getVehicleState());
        return new BusArrival(state, model.getArrival().getOrder(),
                arrival.getBusStopCount(), arrival.getArrivalTime(), arrival.getVehicleNumber(),
                arrival.getBusStopCount2(), arrival.getArrivalTime2(), arrival.getVehicleNumber2(),
                model.getName());
    }

    private static BusStatus getKakaoStateLabel(String id) {
        switch (id) {
            case "-3": // 점검중
            case "1001": // 정보없음
            case "-2": // 운행종료
                return BusStatus.STOP;
            case "-1": // 차량대기
            case "10": // 회차대기지연
                return BusStatus.WAITING;
            case "1002": // 곧도착
            case "1003": // 우회운행중
                return BusStatus.RUN;
        }
        return BusStatus.RUN;
    }
}
