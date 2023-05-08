package com.dku.council.domain.user.model;

public enum UserStatus {
    /**
     * 정상 활성화된 계정
     */
    ACTIVE,

    /**
     * 비활성화된 계정
     */
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
