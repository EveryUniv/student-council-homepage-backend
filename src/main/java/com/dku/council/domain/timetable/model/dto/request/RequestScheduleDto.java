package com.dku.council.domain.timetable.model.dto.request;

import com.dku.council.domain.timetable.model.TimeScheduleType;
import com.dku.council.domain.timetable.model.dto.TimePromise;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class RequestScheduleDto {

    @Schema(description = "일정 이름. 수업이면 무시됩니다.", example = "알바")
    private final String name;

    @Schema(description = "메모. 수업이면 무시됩니다.", example = "일정 메모")
    private final String memo;

    @Schema(description = "일정 타입")
    private final TimeScheduleType type;

    @Schema(description = "시간과 장소 목록. 수업이면 무시됩니다.")
    private final List<TimePromise> times;

    @Schema(description = "색상. 정해진 포맷은 없습니다.", example = "FFFFFF")
    private final String color;

}
