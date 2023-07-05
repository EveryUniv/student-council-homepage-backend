package com.dku.council.domain.admin.dto;

import com.dku.council.domain.homebus.model.entity.HomeBus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class HomeBusPageDto extends HomeBus {
    private final Long id;

    private final String label;

    private final String path;

    private final String destination;

    private final int totalSeats;

    private final Long needApprovalCnt;

}
