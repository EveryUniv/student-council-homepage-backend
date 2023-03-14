package com.dku.council.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserFindService {

    public void sendIdBySMS(String phone) {

    }

    public void sendPwdCodeBySMS(String phone) {

    }

    public void verifyPwdCode(String phone, String code) {

    }
}
