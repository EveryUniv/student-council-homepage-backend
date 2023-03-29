package com.dku.council.util;

import com.dku.council.global.config.jackson.JacksonDateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class ObjectMapperGenerator {
    public static ObjectMapper create() {
        JacksonDateTimeFormatter formatter = new JacksonDateTimeFormatter();
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        formatter.configure(builder);
        return builder.build();
    }
}
