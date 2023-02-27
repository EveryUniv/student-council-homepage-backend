package com.dku.council.global.auth.jwt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtAuthentication implements AppAuthentication {
    private Long userId;
    private String userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : userRole.split(",")) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public String getUserRole() {
        return userRole;
    }

    @Override
    public Object getCredentials() {
        return userId;
    }

    @Override
    public Object getDetails() {
        return userId;
    }

    @Override
    public Object getPrincipal() {
        return userId;
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
