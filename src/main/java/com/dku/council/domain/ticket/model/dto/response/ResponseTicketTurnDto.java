package com.dku.council.domain.ticket.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseTicketTurnDto {

    @Schema(description = "예매 순서", example = "5")
    private final int turn;

    public ResponseTicketTurnDto(int turn) {
        this.turn = turn;
    }
}
