package com.dku.council.mock;

import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.util.EntityUtil;


public class HomeBusMock {
    public static final String LABEL = "test 호차";
    public static final String PATH = "경로1, 경로2, 경로3";
    public static final String DESTINATION = "test 종착지";
    public static final int TOTAL_SEATS = 199;

    public static HomeBus create() {
        return createWithSeats(TOTAL_SEATS);
    }

    public static HomeBus createWithSeats(int totalSeats) {
        HomeBus homeBus = HomeBus.builder()
                .label(LABEL)
                .path(PATH)
                .destination(DESTINATION)
                .totalSeats(totalSeats)
                .build();
        EntityUtil.injectId(HomeBus.class, homeBus, RandomGen.nextLong());
        return homeBus;
    }

    public static HomeBus create(Long id) {
        HomeBus homeBus = HomeBus.builder()
                .label(LABEL)
                .path(PATH)
                .destination(DESTINATION)
                .totalSeats(TOTAL_SEATS)
                .build();
        EntityUtil.injectId(HomeBus.class, homeBus, id);
        return homeBus;
    }

    public static HomeBusDto createDummyDto(Long id) {
        return new HomeBusDto(HomeBus.builder()
                .label("label")
                .path("path1, path2")
                .destination("destination")
                .totalSeats(45).build(), 30, HomeBusStatus.NONE);
    }

}