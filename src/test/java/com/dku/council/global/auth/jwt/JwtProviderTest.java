package com.dku.council.global.auth.jwt;

import com.dku.council.domain.UserRole;
import com.dku.council.domain.user.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    @Test
    @Disabled
    void test1() {
        JwtProvider jwtProvider = new JwtProvider(3L, 10L, "hello");
        AuthenticationToken issue = jwtProvider.issue(User.builder().role(UserRole.ADMIN).build());
        AuthenticationToken authenticationToken = jwtProvider.reIssue(issue);
    }

}
