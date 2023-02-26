package com.dku.council.domain.user.model.dto.response;

import lombok.Getter;

@Getter
public class ResponseVerifyStudentDto {

    private final String signupToken;
    private final ResponseStudentInfoDto student;

    public ResponseVerifyStudentDto(String signupToken, ResponseStudentInfoDto student) {
        this.signupToken = signupToken;
        this.student = student;
    }
}
