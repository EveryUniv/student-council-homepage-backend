package com.dku.council.infra.nhn.service;

public interface NHNEmailService {
    void sendMessage(String studentId, String title, String text);


}
