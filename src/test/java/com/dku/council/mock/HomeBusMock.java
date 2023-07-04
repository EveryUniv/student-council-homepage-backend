package com.dku.council.mock;

import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.util.EntityUtil;

public class HomeBusMock {
    public static final String label = "test 호차";
    public static final String path = "경로1, 경로2, 경로3";
    public static final String destination = "test 종착지";
    public static final int totalSeats = 199;

    public static HomeBus create(){
        HomeBus homeBus = HomeBus.builder()
                .label(label)
                .path(path)
                .destination(destination)
                .totalSeats(totalSeats)
                .build();
        EntityUtil.injectId(HomeBus.class, homeBus, RandomGen.nextLong());
        return homeBus;
    }

    public static HomeBus create(Long id){
        HomeBus homeBus = HomeBus.builder()
                .label(label)
                .path(path)
                .destination(destination)
                .totalSeats(totalSeats)
                .build();
        EntityUtil.injectId(HomeBus.class, homeBus, id);
        return homeBus;
    }

}
