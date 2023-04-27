package com.dku.council.domain.ticket.model.dto;

import com.dku.council.domain.ticket.model.entity.TicketEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class TicketEventDto {

    @Schema(description = "티켓 이벤트 아이디")
    private final Long id;

    @Schema(description = "티켓 이벤트 이름")
    private final String name;

    @Schema(description = "시작 시각")
    private final LocalDateTime from;

    @Schema(description = "종료 시각")
    private final LocalDateTime to;


    public TicketEventDto(TicketEvent e) {
        this.id = e.getId();
        this.name = e.getName();
        this.from = e.getStartAt();
        this.to = e.getEndAt();
    }
}
