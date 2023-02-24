package com.dku.council.domain.user.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = {@JsonCreator})
public class RequestSendSMSCodeDto {

    // TODO Test it
    @NotBlank
    @Pattern(regexp = "\\d{3}-*\\d{4}-*\\d{4}")
    private final String phoneNumber;
}
