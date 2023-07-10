package com.dku.council.domain.homebus.model.dto;

import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class HomeBusDto {

    @Schema(description = "버스 아이디", example = "2")
    private final Long id;

    @Schema(description = "버스 호차번호", example = "1")
    private final String label;

    @Schema(description = "경로 목록")
    private final List<String> path;

    @Schema(description = "목적지", example = "우리집")
    private final String destination;

    @Schema(description = "잔여석", example = "31")
    private final int remainingSeats;

    @Schema(description = "총 좌석", example = "100")
    private final int totalSeats;

    @Schema(description = "신청 상태", example = "NEED_APPROVAL")
    private final HomeBusStatus status;


    public HomeBusDto(HomeBus entity, int remainingSeats, HomeBusStatus status) {
        this.id = entity.getId();
        this.label = entity.getLabel();
        this.path = toList(entity.getPath());
        this.destination = entity.getDestination();
        this.remainingSeats = remainingSeats;
        this.totalSeats = entity.getTotalSeats();
        this.status = status;
    }

    private List<String> toList(String text) {
        return Arrays.stream(text.split(","))
                .map(String::strip)
                .collect(Collectors.toList());
    }
}
