package com.dku.council.domain.user.repository;

import java.util.Optional;

public interface SignupAuthRepository {

    /**
     * 회원가입 토큰을 키로, 인증 정보를 저장합니다.
     *
     * @param signupToken 회원가입 토큰
     * @param authName    인증 정보 이름 (구분자)
     * @param data        인증 정보 데이터
     */
    void setAuthPayload(String signupToken, String authName, Object data);

    /**
     * 회원가입 토큰을 통해 저장된 인증 정보를 가져옵니다.
     *
     * @param signupToken 회원가입 토큰
     * @param authName    인증 정보 이름 (구분자)
     */
    <T> Optional<T> getAuthPayload(String signupToken, String authName, Class<T> payloadClass);

    /**
     * 회원가입 토큰을 통해 저장된 인증 정보를 삭제합니다.
     *
     * @param signupToken 회원가입 토큰
     * @param authName    인증 정보 이름 (구분자)
     * @return 삭제된 경우 true, 아니면 false반환
     */
    boolean deleteAuthPayload(String signupToken, String authName);
}
