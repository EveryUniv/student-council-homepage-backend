package com.dku.council.domain.user.model.dto.response;

import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.role.UserRole;
import lombok.Getter;

@Getter
public class ResponseLoginDto {
    private final String accessToken;
    private final String refreshToken;
    private final boolean isAdmin;
    private final String userName;
    private final String studentId;
    private final String major;
    private final String department;

    public ResponseLoginDto(AuthenticationToken token, UserRole role, User user) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
        this.isAdmin = role.isAdmin();
        this.userName = user.getName();
        this.studentId = user.getStudentId();
        this.major = user.getMajor().getName();
        this.department = user.getMajor().getDepartment();
    }
}
