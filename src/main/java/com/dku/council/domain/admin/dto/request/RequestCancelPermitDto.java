package com.dku.council.domain.admin.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestCancelPermitDto {
    private final Long ticketId;
}
