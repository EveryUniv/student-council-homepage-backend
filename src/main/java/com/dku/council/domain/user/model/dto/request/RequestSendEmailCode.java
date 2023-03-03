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
public class RequestSendEmailCode {
    @NotBlank
    @Pattern(regexp = "^\\d{8}$", message = "학번을 정확히 입력해주세요")
    private final String studentId;
}
