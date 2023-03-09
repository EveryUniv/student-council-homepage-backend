package com.dku.council.global.config.jackson;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JacksonDateTimeFormatter implements JacksonFormatConfigurer {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN);

    @Override
    public void configure(Jackson2ObjectMapperBuilder builder) {
        builder.simpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        builder.serializers(new LocalDateSerializer(DATE_FORMAT));
        builder.serializers(new LocalDateTimeSerializer(DATE_TIME_FORMAT));
        builder.deserializers(new LocalDateDeserializer(DATE_FORMAT));
        builder.deserializers(new LocalDateTimeDeserializer(DATE_TIME_FORMAT));
    }

    public static String serialize(LocalDateTime datetime) {
        return DATE_TIME_FORMAT.format(datetime);
    }
}
