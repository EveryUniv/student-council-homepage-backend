package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.SMSAuth;

import java.time.Instant;
import java.util.Optional;

public interface UserFindRepository {

    /**
     * 인증 코드를 저장합니다.
     *
     * @param token 토큰
     * @param code  인증 코드
     * @param now   현재 시각
     */
    void setAuthCode(String token, String code, String phone, Instant now);

    /**
     * 토큰을 통해 저장된 인증 코드를 조회합니다.
     *
     * @param token 토큰
     * @param now   현재 시각
     */
    Optional<SMSAuth> getAuthCode(String token, Instant now);

    /**
     * 저장된 인증 코드를 삭제합니다.
     *
     * @param token 토큰
     */
    void deleteAuthCode(String token);
}
