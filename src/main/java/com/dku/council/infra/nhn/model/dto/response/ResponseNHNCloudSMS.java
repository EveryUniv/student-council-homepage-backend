package com.dku.council.infra.nhn.model.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ResponseNHNCloudSMS {
    private Header header;
    private Body body;

    @Getter
    @NoArgsConstructor(access = PROTECTED)
    public static class Header {
        private boolean isSuccessful;
        private int resultCode;
        private String resultMessage;

        // isSuccessful은 @Getter tag로 인해 isSuccessful() getter가 만들어진다.
        // 그래서 파싱할 때 제대로 파싱이 안된다.
        // 따로 getIsSuccessful을 만들어주어야 함.
        public boolean getIsSuccessful() {
            return isSuccessful;
        }
    }

    @Getter
    @NoArgsConstructor(access = PROTECTED)
    public static class Body {
        private Data data;

        @Getter
        @NoArgsConstructor(access = PROTECTED)
        public static class Data {
            private String requestId;
            private String statusCode;
            private List<SendResult> sendResultList;

            @Getter
            @NoArgsConstructor(access = PROTECTED)
            public static class SendResult {
                private String recipientNo;
                private int resultCode;
                private String resultMessage;
            }
        }
    }
}
