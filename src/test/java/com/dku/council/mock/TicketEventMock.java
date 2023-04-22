package com.dku.council.mock;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.util.EntityUtil;

import java.time.LocalDateTime;

public class TicketEventMock {

    public static TicketEventDto createDummyDto(Long id) {
        return new TicketEventDto(id, "name",
                LocalDateTime.of(2021, 1, 1, 0, 0),
                LocalDateTime.of(2021, 3, 1, 0, 0));
    }

    public static TicketEvent createDummy() {
        return createDummy("name");
    }

    public static TicketEvent createDummy(String name) {
        return createDummy(RandomGen.nextLong(), name);
    }

    public static TicketEvent createDummy(Long id, String name) {
        TicketEvent dummy = new TicketEvent(name,
                LocalDateTime.of(2021, 1, 1, 0, 0),
                LocalDateTime.of(2021, 3, 1, 0, 0),
                1000);
        EntityUtil.injectId(TicketEvent.class, dummy, id);
        return dummy;
    }
}
