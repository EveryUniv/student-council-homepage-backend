package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.NotSMSAuthorizedException;
import org.springframework.stereotype.Service;

@Service
public class SMSVerificationService {

    /**
     * 회원가입 토큰을 기반으로 인증된 휴대폰 정보를 가져옵니다. 휴대폰 인증이 되어있지 않으면 Exception이 발생합니다.
     * 이 메서드는 회원가입 진행자를 대상으로 합니다.
     *
     * @param signupToken 회원가입 토큰
     * @throws NotSMSAuthorizedException 휴대폰 인증을 하지 않았을 경우
     */
    public String getPhoneNumber(String signupToken) throws NotSMSAuthorizedException {
        // TODO Implementation
        return null;
    }
}
