package com.dku.council.domain.timetable.model.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class RequestLectureDto {

    private final Long id;
    private final String color;
}
