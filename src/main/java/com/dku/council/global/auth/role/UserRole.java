package com.dku.council.global.auth.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dku.council.global.auth.role.UserRoleNames.*;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER(ROLE_USER),
    GUEST(ROLE_GUEST),
    ADMIN(combine(ROLE_ADMIN, ROLE_USER));

    private final String role;

    private static final Map<String, UserRole> BY_LABEL =
            Stream.of(values()).collect(Collectors.toMap(UserRole::getRole, e -> e));

    public static UserRole of(String name) {
        return BY_LABEL.get(name);
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
