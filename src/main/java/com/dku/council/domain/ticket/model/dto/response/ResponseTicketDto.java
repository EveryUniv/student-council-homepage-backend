package com.dku.council.domain.ticket.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseTicketDto {

    @Schema(description = "티켓 ID", example = "8225")
    private final Long id;

    @Schema(description = "이름", example = "홍길동")
    private final String name;

    @Schema(description = "학과", example = "컴퓨터공학과")
    private final String major;

    @Schema(description = "학번", example = "32112222")
    private final String studentId;

    @Schema(description = "티켓 발급 여부")
    private final boolean issued;

    @Schema(description = "예매 순서", example = "5")
    private final int turn;

    public ResponseTicketDto(Long id, String name, String major,
                             String studentId, boolean issued, int turn) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.studentId = studentId;
        this.issued = issued;
        this.turn = turn;
    }
}
