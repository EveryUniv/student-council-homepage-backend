package com.dku.council.infra.dku.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudentInfo {
    private final String studentName;
    private final String studentId;
    private final int yearOfAdmission;

    private final String departmentName;
    private final String majorName;
}
