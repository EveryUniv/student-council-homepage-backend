package com.dku.council.global.auth.jwt;

import com.dku.council.domain.user.model.Major;
import com.dku.council.domain.user.model.UserRole;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.entity.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    @Test
    @Disabled
    void test1() {
        JwtProvider jwtProvider = new JwtProvider(3L, 10L, "hello");
        User user = User.builder()
                .name("테스트")
                .studentId("32171111")
                .phone("010-1234-5678")
                .status(UserStatus.ACTIVE)
                .major(Major.COMPUTER_SCIENCE)
                .password("TestPwd")
                .role(UserRole.ADMIN).build();
        AuthenticationToken issue = jwtProvider.issue(user);
        AuthenticationToken authenticationToken = jwtProvider.reIssue(issue);
    }

}
