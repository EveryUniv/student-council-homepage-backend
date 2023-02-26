package com.dku.council.infra.dku.model;

import com.dku.council.domain.user.model.MajorData;
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

    private MajorData majorData;
    private String notRecognizedDepartment;
    private String notRecognizedMajor;

    public StudentInfo(String studentName, String studentId, int yearOfAdmission, MajorData majorData) {
        this(studentName, studentId, yearOfAdmission, majorData, null, null);
    }

    public StudentInfo(String studentName, String studentId, int yearOfAdmission, String notRecognizedMajor, String notRecognizedDepartment) {
        this(studentName, studentId, yearOfAdmission, MajorData.NO_DATA, notRecognizedDepartment, notRecognizedMajor);
    }
}
