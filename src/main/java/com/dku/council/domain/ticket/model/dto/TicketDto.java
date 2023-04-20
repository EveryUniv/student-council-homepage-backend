package com.dku.council.domain.ticket.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TicketDto {
    private final Long userId;
    private final Long eventId;
    private final int turn;
}
