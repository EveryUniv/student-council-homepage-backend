package com.dku.council.mock.user;

import com.dku.council.global.auth.jwt.JwtAuthentication;
import com.dku.council.global.auth.role.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuth {
    public static void withUser(Long userId) {
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthentication(userId, UserRole.USER));
    }

    public static void withAdmin(Long userId) {
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthentication(userId, UserRole.ADMIN));
    }
}
