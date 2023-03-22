package com.dku.council.infra.bus.model;

public enum BusStatus {
    /**
     * 운행중
     */
    RUN,

    /**
     * 도착 지연(회차대기, 점검 등)
     */
    WAITING,

    /**
     * 예측된 시각
     */
    PREDICT,

    /**
     * 운행 종료
     */
    STOP
}
