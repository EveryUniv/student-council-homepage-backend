package com.dku.council.global.auth.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
public class JwtAuthentication implements Authentication {
    String userId;
    String userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : userRole.split(",")) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return Long.parseLong(userId);
    }

    @Override
    public Object getDetails() {
        return Long.parseLong(userId);
    }

    @Override
    public Object getPrincipal() {
        return Long.parseLong(userId);
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
