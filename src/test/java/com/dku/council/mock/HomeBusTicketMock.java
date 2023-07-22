package com.dku.council.mock;

import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.util.EntityUtil;

public class HomeBusTicketMock {

    public static HomeBusTicket create(HomeBus homeBus, HomeBusStatus status) {
        HomeBusTicket homeBusTicket = HomeBusTicket.builder()
                .user(UserMock.createDummyMajor())
                .bus(homeBus)
                .status(status)
                .build();
        EntityUtil.injectId(HomeBusTicket.class, homeBusTicket, RandomGen.nextLong());
        return homeBusTicket;
    }
}
