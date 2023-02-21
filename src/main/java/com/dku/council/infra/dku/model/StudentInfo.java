package com.dku.council.infra.dku.model;

import com.dku.council.domain.user.model.Major;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StudentInfo {
    private final String studentId;
    private final int yearOfAdmission;
    private final Major major;

    public StudentInfo(String studentId, int yearOfAdmission, Major major) {
        this.studentId = studentId;
        this.yearOfAdmission = yearOfAdmission;
        this.major = major;
    }
}
