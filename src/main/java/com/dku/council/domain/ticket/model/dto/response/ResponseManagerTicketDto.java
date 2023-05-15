package com.dku.council.domain.ticket.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseManagerTicketDto {

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

    @Schema(description = "전송된 인증 코드. 이미 발급된 티켓의 경우 빈 문자열 반환", example = "123456")
    private final String code;

    @Schema(description = "티켓 이벤트 ", example = "1111")
    private final Long eventId;

    public ResponseManagerTicketDto(ResponseTicketDto ticket, String code, Long eventId) {
        this.id = ticket.getId();
        this.name = ticket.getName();
        this.major = ticket.getMajor();
        this.studentId = ticket.getStudentId();
        this.issued = ticket.isIssued();
        this.turn = ticket.getTurn();
        this.code = code;
        this.eventId = eventId;
    }
}
