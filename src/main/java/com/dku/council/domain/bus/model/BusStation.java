package com.dku.council.domain.bus.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BusStation {
    DKU_GATE("단국대정문", "228001978"),
    BEAR_STATUE("곰상", "228001980");

    private final String name;
    private final String nodeId;

    /**
     * 이름으로 BusStation을 찾습니다.
     *
     * @param name 찾을 bus station name
     * @return 찾은 BusStation. 없으면 null
     */
    public static BusStation of(String name) {
        for (BusStation station : values()) {
            if (station.name.equals(name)) {
                return station;
            }
        }
        return null;
    }
}
