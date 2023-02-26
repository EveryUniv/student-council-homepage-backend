package com.dku.council.domain.user.model.dto.response;

import com.dku.council.domain.user.model.MajorData;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Getter
public class ResponseStudentInfoDto {

    private final String studentName;
    private final String studentId;
    private final String major;

    private ResponseStudentInfoDto(MessageSource messageSource, String studentName, String studentId, MajorData majorData) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.major = majorData.getName(messageSource);
    }

    private ResponseStudentInfoDto(String studentName, String studentId, String notRecognizedMajor) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.major = notRecognizedMajor;
    }

    public static ResponseStudentInfoDto from(MessageSource messageSource, StudentInfo info) {
        MajorData infoMajorData = info.getMajorData();
        if (infoMajorData == null) {
            return new ResponseStudentInfoDto(info.getStudentName(),
                    info.getStudentId(),
                    info.getNotRecognizedMajor());
        } else {
            return new ResponseStudentInfoDto(messageSource,
                    info.getStudentName(),
                    info.getStudentId(),
                    infoMajorData);
        }
    }
}
