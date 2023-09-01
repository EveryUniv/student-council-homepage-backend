package com.dku.council.infra.nhn.model.dto.response;

import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudSMS.Body.Data.SendResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@RequiredArgsConstructor(access = PROTECTED)
public class ResponseNHNCloudMMS {
    private final Header header;
    private final Body body;

    @Getter
    @RequiredArgsConstructor(access = PROTECTED)
    public static class Header {
        private final boolean isSuccessful;
        private final int resultCode;
        private final String resultMessage;

        public boolean getIsSuccessful() {
            return isSuccessful;
        }
    }

    @Getter
    @RequiredArgsConstructor(access = PROTECTED)
    public static class Body{
        private final Data data;

        @Getter
        @RequiredArgsConstructor(access = PROTECTED)
        public static class Data {
            private final String requestId;
            private final String statusCode;
            private final List<SendResult> sendResultList;

            @Getter
            @RequiredArgsConstructor(access = PROTECTED)
            public static class SendResult {
                private final String recipientNo;
                private final int resultCode;
                private final String resultMessage;
            }
        }
    }


}
