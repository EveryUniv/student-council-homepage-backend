package com.dku.council.domain.bus.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Bus {
    B102("102", "206000007"),
    B1101("1101", "234000879"),
    B1101N("1101N", "228000418"),
    B7007_1("7007-1", "228000393"),
    B8100("8100", "234000878"),
    B720_3("720-3", "234000068");

    private final String name;
    private final String routeId;

    /**
     * routeId로 Bus를 찾습니다.
     *
     * @param routeId 찾을 bus routeId
     * @return 찾은 Bus. 없으면 null
     */
    public static Bus of(String routeId) {
        for (Bus bus : values()) {
            if (bus.routeId.equals(routeId)) {
                return bus;
            }
        }
        return null;
    }
}
