package com.dku.council.domain.user.model.dto.request;

import com.dku.council.domain.user.model.MajorData;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = {@JsonCreator})
public class RequestVerifyEmailCodeDto {

    @NotBlank(message = "이름을 입력해주세요")
    private final String studentName;

    @NotBlank(message = "학번을 입력해주세요")
    @Pattern(regexp = "^\\d{8}$")
    private final String studentId;

    @NotBlank(message = "학과를 입력해주세요")
    private final MajorData majorData;

    private final int yearOfAdmission = 23;

    @NotBlank
    @Pattern(regexp = "^\\d{5}$")
    private final String emailCode;
}
