package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.NotDKUAuthorizedException;
import com.dku.council.infra.dku.model.StudentInfo;
import org.springframework.stereotype.Service;

@Service
public class DKUAuthService {

    /**
     * 회원가입 토큰을 기반으로 인증된 학생 정보를 가져옵니다. 학생인증이 되어있지 않으면 Exception이 발생합니다.
     * 이 메서드는 회원가입 진행자를 대상으로 합니다.
     *
     * @param signupToken 회원가입 토큰
     * @throws NotDKUAuthorizedException 학생 인증을 하지 않았을 경우
     */
    public StudentInfo getDKUStudentInfo(String signupToken) throws NotDKUAuthorizedException {
        // TODO Implementation
        return null;
    }
}
