package com.dku.council.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

public class MvcMockResponse {
    public static <T> T getResponse(ObjectMapper objectMapper, MvcResult result, Class<T> responseType) throws UnsupportedEncodingException, JsonProcessingException {
        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, responseType);
    }
}
