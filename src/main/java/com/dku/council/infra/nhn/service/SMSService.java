package com.dku.council.infra.nhn.service;

public interface SMSService {
    void sendSMS(String phone, String body);
}
