package com.dku.council.domain.user.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class RequestSendSMSCodeDto {

    // TODO Test it
    @NotBlank
    @Pattern(regexp = "\\d{3}-*\\d{4}-*\\d{4}")
    private final String phoneNumber;

    @JsonCreator
    public RequestSendSMSCodeDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
