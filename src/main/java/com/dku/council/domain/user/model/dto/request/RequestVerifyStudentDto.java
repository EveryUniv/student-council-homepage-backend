package com.dku.council.domain.user.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestVerifyStudentDto {

    @NotBlank
    private final String dkuStudentId;

    @NotBlank
    private final String dkuPassword;
}
