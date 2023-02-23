package com.dku.council.infra.dku.model;

import com.dku.council.domain.user.model.Major;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StudentInfo {
    private final String studentName;
    private final String studentId;
    private final int yearOfAdmission;
    private final Major major;
    private final String notRecognizedMajor;

    public StudentInfo(String studentName, String studentId, int yearOfAdmission, Major major) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.yearOfAdmission = yearOfAdmission;
        this.major = major;
        this.notRecognizedMajor = null;
    }

    public StudentInfo(String studentName, String studentId, int yearOfAdmission, String notRecognizedMajor) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.yearOfAdmission = yearOfAdmission;
        this.major = Major.NO_DATA;
        this.notRecognizedMajor = notRecognizedMajor;
    }
}
