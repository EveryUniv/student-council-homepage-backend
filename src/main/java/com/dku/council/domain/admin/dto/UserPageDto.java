package com.dku.council.domain.admin.dto;

import com.dku.council.domain.user.model.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPageDto {
    private final Long id;
    private final String name;
    private final String nickname;
    private final String studentId;
    private final String major;
    private final String phone;
    private final String status;
    private final String userRole;

    public UserPageDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.studentId = user.getStudentId();
        this.major = user.getMajor().getName();
        this.phone = user.getPhone();
        this.status = user.getStatus().toString();
        this.userRole = user.getUserRole().toString();
    }

}
