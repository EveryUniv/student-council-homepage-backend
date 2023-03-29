package com.dku.council.domain.user.model.dto.response;

import com.dku.council.domain.user.model.UserSignupInfo;
import lombok.Getter;

@Getter
public class ResponseScrappedStudentInfoDto {

    private final String studentName;
    private final String studentId;
    private final String major;

    public ResponseScrappedStudentInfoDto(UserSignupInfo info) {
        this.studentName = info.getStudentName();
        this.studentId = info.getStudentId();
        this.major = info.getMajorName();
    }
}
