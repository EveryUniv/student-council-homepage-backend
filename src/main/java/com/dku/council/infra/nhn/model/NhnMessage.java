package com.dku.council.infra.nhn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class NhnMessage {

    private String senderAddress;
    private String senderName;
    private String title;
    private String body;
    private Receiver receiver;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Receiver{
        private String receiveMailAddr;
    }
}