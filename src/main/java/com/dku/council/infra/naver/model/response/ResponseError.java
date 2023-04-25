package com.dku.council.infra.naver.model.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@RequiredArgsConstructor(access = PROTECTED)
public class ResponseError {
    private final Boolean result;
    private final String errorMessage;
    private final String errorCode;

    public static ResponseError parseFromJson(ObjectMapper mapper, String json) throws JsonProcessingException {
        return mapper.readValue(json, ResponseError.class);
    }
}
