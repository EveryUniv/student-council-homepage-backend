package com.dku.council.infra.nhn.model.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ResponseNHNCloudSMS {
    private final Header header;
    private final Body body;

    public ResponseNHNCloudSMS(Header header, Body body) {
        this.header = header;
        this.body = body;
    }

    @Getter
    public static class Header {
        private final boolean isSuccessful;
        private final int resultCode;
        private final String resultMessage;

        public Header(boolean isSuccessful, int resultCode, String resultMessage) {
            this.isSuccessful = isSuccessful;
            this.resultCode = resultCode;
            this.resultMessage = resultMessage;
        }

        // isSuccessful은 @Getter tag로 인해 isSuccessful() getter가 만들어진다.
        // 그래서 파싱할 때 제대로 파싱이 안된다.
        // 따로 getIsSuccessful을 만들어주어야 함.
        public boolean getIsSuccessful() {
            return isSuccessful;
        }
    }

    @Getter
    public static class Body {
        private final Data data;

        public Body(Data data) {
            this.data = data;
        }

        @Getter
        public static class Data {
            private final String requestId;
            private final String statusCode;
            private final List<SendResult> sendResultList;

            public Data(String requestId, String statusCode, List<SendResult> sendResultList) {
                this.requestId = requestId;
                this.statusCode = statusCode;
                this.sendResultList = sendResultList;
            }

            @Getter
            public static class SendResult {
                private final String recipientNo;
                private final String resultCode;
                private final String resultMessage;

                public SendResult(String recipientNo, String resultCode, String resultMessage) {
                    this.recipientNo = recipientNo;
                    this.resultCode = resultCode;
                    this.resultMessage = resultMessage;
                }
            }
        }
    }
}
