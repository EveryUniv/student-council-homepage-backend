package com.dku.council.domain.user.model.dto.response;

import lombok.Getter;

@Getter
public class ResponseVerifyStudentDto {

    private final String signupToken;
    private final ResponseScrappedStudentInfoDto student;

    public ResponseVerifyStudentDto(String signupToken, ResponseScrappedStudentInfoDto student) {
        this.signupToken = signupToken;
        this.student = student;
    }
}
