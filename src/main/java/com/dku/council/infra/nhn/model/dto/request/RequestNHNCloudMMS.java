package com.dku.council.infra.nhn.model.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestNHNCloudMMS {
    private final String title;
    private final String body;
    private final String sendNo;
    private final List<Recipient> recipientList;

    public RequestNHNCloudMMS(String title, String senderPhone, String phone, String body) {
        this.title = title;
        this.body = body;
        this.sendNo = senderPhone;
        this.recipientList = List.of(new Recipient(phone));
    }

    @Getter
    public static class Recipient {
        private final String recipientNo;

        public Recipient(String recipientNo) {
            this.recipientNo = recipientNo.replaceAll("-", "");
        }
    }
}
