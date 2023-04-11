package com.dku.council.global.auth.jwt;

import com.dku.council.global.auth.role.UserRole;
import org.springframework.security.core.Authentication;

public interface AppAuthentication extends Authentication {

    Long getUserId();

    UserRole getUserRole();

    boolean isAdmin();
}
