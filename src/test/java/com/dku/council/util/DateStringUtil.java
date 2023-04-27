package com.dku.council.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

public class DateStringUtil {
    public static String toString(ObjectMapper mapper, LocalDateTime dateTime) throws JsonProcessingException {
        String result = mapper.writeValueAsString(dateTime);
        result = result.substring(1, result.length() - 1);
        return result;
    }
}
