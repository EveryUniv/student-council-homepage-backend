package com.dku.council.domain.ticket.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TicketDto {

    @Schema(description = "대기 순번", example = "5")
    private final int turn;

    public TicketDto(int turn) {
        this.turn = turn;
    }
}
