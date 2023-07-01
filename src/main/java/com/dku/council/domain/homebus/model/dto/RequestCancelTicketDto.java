package com.dku.council.domain.homebus.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestCancelTicketDto {

    @NotBlank
    private final String depositor;

    @NotBlank
    private final String accountNum;

    @NotBlank
    private final String bankName;
}
