package com.dku.council.domain.ticket.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseTicketDto {

    @Schema(description = "대기 순번", example = "5")
    private final int turn;

    public ResponseTicketDto(int turn) {
        this.turn = turn;
    }
}
