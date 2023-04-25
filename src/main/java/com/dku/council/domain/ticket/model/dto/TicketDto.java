package com.dku.council.domain.ticket.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class TicketDto {
    private final Long userId;
    private final Long eventId;
    private final int turn;
}
