package com.dku.council.mock;

import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.entity.HomeBus;

public class HomeBusMock {

    public static HomeBusDto createDummyDto(Long id) {
        return new HomeBusDto(HomeBus.builder()
                .label("label")
                .path("[path1, path2]")
                .destination("destination")
                .totalSeats(45).build(), 30);
    }
}
