package com.dku.council.domain.bus.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Bus {
    GG_102("102", "206000007"),
    GG_1101("1101", "234000879"),
    GG_1101N("1101N", "228000418"),
    GG_7007_1("7007-1", "228000393"),
    GG_8100("8100", "234000878"),
    GG_720_3("720-3", "234000068"),
    T_24("24", "B39979"),
    DKU_SHUTTLE("Shuttle Bus", "");

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
