package com.dku.council.global.auth.jwt;

import com.dku.council.domain.UserRole;
import com.dku.council.domain.user.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JwtAuthenticationTokenProviderTest {

    @Test
    @Disabled
    void test1() {
        JwtAuthenticationTokenProvider jwtAuthenticationTokenProvider = new JwtAuthenticationTokenProvider(3L, 10L, "hello");
        AuthenticationToken issue = jwtAuthenticationTokenProvider.issue(new User(123L, UserRole.ADMIN));
        AuthenticationToken authenticationToken = jwtAuthenticationTokenProvider.reIssue(issue);
    }

}
