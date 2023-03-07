package com.dku.council.domain.user.model.dto.response;

import com.dku.council.domain.user.model.MajorData;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Getter
public class ResponseScrappedStudentInfoDto {

    private final String studentName;
    private final String studentId;
    private final String major;

    private ResponseScrappedStudentInfoDto(MessageSource messageSource, String studentName, String studentId, MajorData majorData) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.major = majorData.getName(messageSource);
    }

    private ResponseScrappedStudentInfoDto(String studentName, String studentId, String notRecognizedMajor) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.major = notRecognizedMajor;
    }

    public static ResponseScrappedStudentInfoDto from(MessageSource messageSource, StudentInfo info) {
        MajorData infoMajorData = info.getMajorData();
        if (infoMajorData == null) {
            return new ResponseScrappedStudentInfoDto(info.getStudentName(),
                    info.getStudentId(),
                    info.getNotRecognizedMajor());
        } else {
            return new ResponseScrappedStudentInfoDto(messageSource,
                    info.getStudentName(),
                    info.getStudentId(),
                    infoMajorData);
        }
    }
}
