package com.dku.council.domain.user.model;

import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class UserInfo {
    private final String name;
    private final String nickname;
    private final String studentId;
    private final String phone;
    private final MajorInfo major;
    private final int yearOfAdmission;
    private final String academicStatus;
    private final UserStatus status;

    public UserInfo(User user) {
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.studentId = user.getStudentId();
        this.phone = user.getPhone();
        this.major = new MajorInfo(user.getMajor());
        this.yearOfAdmission = user.getYearOfAdmission();
        this.academicStatus = user.getAcademicStatus();
        this.status = user.getStatus();
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class MajorInfo {
        private final String name;
        private final String department;

        public MajorInfo(Major major) {
            this.name = major.getName();
            this.department = major.getDepartment();
        }
    }
}
