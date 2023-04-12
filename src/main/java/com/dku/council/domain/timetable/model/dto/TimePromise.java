package com.dku.council.domain.timetable.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
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

    @JsonIgnore
    public Duration getDuration() {
        return Duration.between(start, end).abs();
    }

    public boolean isConflict(TimePromise other) {
        return other.week == this.week && other.start.isBefore(this.end) && other.end.isAfter(this.start);
    }
}
