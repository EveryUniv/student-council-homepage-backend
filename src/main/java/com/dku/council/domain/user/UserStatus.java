package com.dku.council.domain.user;

public enum UserStatus {
    /**
     * 정상 활성화된 계정
     */
    ACTIVE,

    /**
     * 사용자 인증 진행중인 계정
     */
    VALIDATING
}
