package com.dku.council.domain.ticket.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TicketDto {

    @Schema(description = "티켓 이벤트 이름", example = "티켓 이벤트")
    private final String name;

    @Schema(description = "대기 순번", example = "5")
    private final int turn;

    public TicketDto(String name, int turn) {
        this.name = name;
        this.turn = turn;
    }
}
