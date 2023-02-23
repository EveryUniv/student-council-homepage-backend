package com.dku.council.domain.user.model.dto.response;

import com.dku.council.domain.user.model.Major;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.MessageSource;

@Getter
@ToString
public class StudentInfoDto {
    private final String studentName;
    private final String studentId;
    private final int yearOfAdmission;
    private final String major;

    public StudentInfoDto(MessageSource messageSource, String studentName, String studentId, int yearOfAdmission, Major major) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.yearOfAdmission = yearOfAdmission;
        this.major = major.getName(messageSource);
    }

    public StudentInfoDto(String studentName, String studentId, int yearOfAdmission, String notRecognizedMajor) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.yearOfAdmission = yearOfAdmission;
        this.major = notRecognizedMajor;
    }
}
