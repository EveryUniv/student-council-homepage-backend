package com.dku.council.domain.ticket.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class TicketDto {
    private final Long userId;
    private final Long eventId;
    private final int turn;
}
