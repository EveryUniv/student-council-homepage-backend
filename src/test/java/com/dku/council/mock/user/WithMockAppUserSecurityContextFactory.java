package com.dku.council.mock.user;

import com.dku.council.global.auth.jwt.JwtAuthentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockAppUserSecurityContextFactory implements WithSecurityContextFactory<WithMockAppUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockAppUser user) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        JwtAuthentication auth = new JwtAuthentication(user.userId(), user.userRole());
        context.setAuthentication(auth);
        return context;
    }
}
