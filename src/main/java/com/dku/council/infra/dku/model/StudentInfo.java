package com.dku.council.infra.dku.model;

import com.dku.council.domain.user.model.Major;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentInfo {
    private String studentName;
    private String studentId;
    private int yearOfAdmission;
    private Major major;
    private String notRecognizedMajor;

    public StudentInfo(String studentName, String studentId, int yearOfAdmission, Major major) {
        this(studentName, studentId, yearOfAdmission, major, null);
    }

    public StudentInfo(String studentName, String studentId, int yearOfAdmission, String notRecognizedMajor) {
        this(studentName, studentId, yearOfAdmission, Major.NO_DATA, notRecognizedMajor);
    }
}
