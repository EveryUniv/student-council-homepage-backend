package com.dku.council.domain.ticket.model.dto.request;

import com.dku.council.domain.ticket.model.entity.TicketEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RequestNewTicketEventDto {

    @NotEmpty
    @Schema(description = "티켓 이벤트 이름", example = "이벤트")
    private final String name;

    @NotNull
    @Schema(description = "시작 시각")
    private final LocalDateTime startAt;

    @NotNull
    @Schema(description = "종료 시각")
    private final LocalDateTime endAt;

    @NotNull
    @Schema(description = "총 티켓 수량")
    private final Integer totalTickets;


    public TicketEvent createEntity() {
        return new TicketEvent(name, startAt, endAt, totalTickets);
    }
}
