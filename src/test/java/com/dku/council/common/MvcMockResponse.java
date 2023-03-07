package com.dku.council.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

public class MvcMockResponse {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T getResponse(MvcResult result, Class<T> responseType) throws UnsupportedEncodingException, JsonProcessingException {
        String content = result.getResponse().getContentAsString();
        return OBJECT_MAPPER.readValue(content, responseType);
    }
}
