package com.dku.council.mock.user;

import com.dku.council.global.auth.role.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAppUserSecurityContextFactory.class)
public @interface WithMockAppUser {
    long userId() default 0L;

    UserRole userRole() default UserRole.USER;
}
