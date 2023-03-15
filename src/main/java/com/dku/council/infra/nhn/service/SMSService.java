package com.dku.council.infra.nhn.service;

// TODO 한 IP로 너무 많이 보내면 인증 못하도록 막아야 함
public interface SMSService {
    void sendSMS(String phone, String body);
}
