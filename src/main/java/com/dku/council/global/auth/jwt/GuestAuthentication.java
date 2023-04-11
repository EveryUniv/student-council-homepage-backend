package com.dku.council.global.auth.jwt;

import com.dku.council.global.auth.role.UserRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class GuestAuthentication implements AppAuthentication {

    public static final Long GUEST_USER_ID = -1L;

    @Override
    public Long getUserId() {
        return GUEST_USER_ID;
    }

    @Override
    public UserRole getUserRole() {
        return UserRole.GUEST;
    }

    @Override
    public boolean isAdmin() {
        return getUserRole().isAdmin();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public Object getCredentials() {
        return GUEST_USER_ID;
    }

    @Override
    public Object getDetails() {
        return GUEST_USER_ID;
    }

    @Override
    public Object getPrincipal() {
        return GUEST_USER_ID;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return null;
    }
}
