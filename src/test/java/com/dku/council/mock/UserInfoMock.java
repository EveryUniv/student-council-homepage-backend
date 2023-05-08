package com.dku.council.mock;

import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.auth.role.UserRole;

public class UserInfoMock {

    public static UserInfo create() {
        return new UserInfo(UserMock.createDummyMajor());
    }

    public static UserInfo create(String academicStatus) {
        User user = User.builder()
                .studentId("11111111")
                .password("password")
                .name("name")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .nickname("nickname")
                .yearOfAdmission(2017)
                .academicStatus(academicStatus)
                .major(MajorMock.create())
                .phone("01011112222")
                .build();

        return new UserInfo(user);
    }
}
