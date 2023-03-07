package com.dku.council.domain.user.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseUserInfoDto {

    private final String studentName;
    private final String yearOfAdmission;
    private final String major;
}
