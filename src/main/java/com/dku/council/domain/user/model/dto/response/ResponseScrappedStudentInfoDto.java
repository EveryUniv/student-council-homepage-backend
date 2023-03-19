package com.dku.council.domain.user.model.dto.response;

import com.dku.council.infra.dku.model.StudentInfo;
import lombok.Getter;

@Getter
public class ResponseScrappedStudentInfoDto {

    private final String studentName;
    private final String studentId;
    private final String major;

    public ResponseScrappedStudentInfoDto(StudentInfo info) {
        this.studentName = info.getStudentName();
        this.studentId = info.getStudentId();
        this.major = info.getMajorName();
    }
}
