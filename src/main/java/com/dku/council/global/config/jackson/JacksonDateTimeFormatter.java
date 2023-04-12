package com.dku.council.global.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JacksonDateTimeFormatter implements JacksonFormatConfigurer {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String TIME_FORMAT_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    public static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN);

    @Override
    public void configure(Jackson2ObjectMapperBuilder builder) {
        builder.simpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        builder.serializers(new LocalDateSerializer(DATE_FORMAT));
        builder.serializers(new LocalTimeSerializer(TIME_FORMAT));
        builder.serializers(new LocalDateTimeSerializer(DATE_TIME_FORMAT));
        builder.serializerByType(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
            @Override
            public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(DATE_TIME_FORMAT.format(zonedDateTime));
            }
        });
        builder.deserializers(new LocalDateDeserializer(DATE_FORMAT));
        builder.deserializers(new LocalTimeDeserializer(TIME_FORMAT));
        builder.deserializers(new LocalDateTimeDeserializer(DATE_TIME_FORMAT));
        builder.deserializerByType(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
            @Override
            public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return (ZonedDateTime) DATE_TIME_FORMAT.parse(p.readValueAs(String.class));
            }
        });
    }

    public static String serialize(LocalDateTime datetime) {
        return DATE_TIME_FORMAT.format(datetime);
    }
}
