package com.dku.council.domain.user.model.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestVerifyEmailCodeDto {
    @NotBlank
    @Pattern(regexp = "^\\d{8}$", message = "학번을 정확히 입력해주세요")
    private final String studentId;

    @NotBlank
    private final String studentName;

    @NotEmpty
    private final Long majorId;

    private final int yearOfAdmission = 23;

    @NotBlank
    private final String academicStatus;

    @NotBlank
    @Pattern(regexp = "^.{5}$", message = "이메일 코드는 5자리 입니다.")
    private final String emailCode;
}
