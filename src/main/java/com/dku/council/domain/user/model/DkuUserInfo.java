package com.dku.council.domain.user.model;

import com.dku.council.infra.dku.model.StudentDuesStatus;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DkuUserInfo {
    private final String studentName;
    private final String studentId;
    private final int yearOfAdmission;
    private final String studentState;
    private final StudentDuesStatus duesStatus;

    private final String majorName;
    private final String departmentName;

    public DkuUserInfo(StudentInfo info, StudentDuesStatus duesStatus) {
        this.studentName = info.getStudentName();
        this.studentId = info.getStudentId();
        this.yearOfAdmission = info.getYearOfAdmission();
        this.studentState = info.getStudentState();
        this.duesStatus = duesStatus;
        this.majorName = info.getMajorName();
        this.departmentName = info.getDepartmentName();
    }
}
