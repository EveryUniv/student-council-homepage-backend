package com.dku.council.domain.ticket.repository.impl;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;

import java.util.List;

public class TicketRedisRepository implements TicketMemoryRepository {

    @Override
    public List<TicketEventDto> findAllEvents() {
        return null;
    }

    @Override
    public List<TicketEventDto> saveEvents(List<TicketEvent> events) {
        return null;
    }

    @Override
    public int enroll(Long userId, Long ticketEventId) {
        return 0;
    }

    @Override
    public int getMyTicket(Long userId, Long ticketEventId) {
        return 0;
    }

    @Override
    public int saveMyTicket(Long userId, Long ticketEventId, int turn) {
        return 0;
    }
}
