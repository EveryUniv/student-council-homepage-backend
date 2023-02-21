package com.dku.council.infra.nhn.model.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestNHNCloudSMS {
    private final String body;
    private final String sendNo;
    private final List<Recipient> recipientList;

    public RequestNHNCloudSMS(String senderPhone, String phone, String body) {
        this.body = body;
        this.sendNo = senderPhone;
        this.recipientList = List.of(new Recipient(phone));
    }

    @Getter
    public static class Recipient {
        private final String recipientNo;

        public Recipient(String recipientNo) {
            // 전화번호에 하이픈(-)을 넣어도 알아서 빼주기.
            this.recipientNo = recipientNo.replaceAll("-", "");
        }
    }
}