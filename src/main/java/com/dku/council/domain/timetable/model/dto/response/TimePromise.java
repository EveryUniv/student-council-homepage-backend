package com.dku.council.domain.timetable.model.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class TimePromise {

    @Schema(description = "시작 시각", example = "09:00:00")
    private final LocalTime start;

    @Schema(description = "종료 시각", example = "13:00:00")
    private final LocalTime end;

    @Schema(description = "요일")
    private final DayOfWeek week;

    @Schema(description = "장소")
    private final String place;


    public static List<TimePromise> parse(ObjectMapper mapper, String json) {
        try {
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, TimePromise.class);
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serialize(ObjectMapper mapper, List<TimePromise> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
