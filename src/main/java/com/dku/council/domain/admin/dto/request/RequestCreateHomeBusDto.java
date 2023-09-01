package com.dku.council.domain.admin.dto.request;

import com.dku.council.domain.homebus.model.entity.HomeBus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor
public class RequestCreateHomeBusDto {
    @NotBlank(message = "라벨을 입력해주세요.")
    @Schema(description = "버스 호차번호", example = "1호차")
    private final String label;


    @Pattern(regexp = "^[^,]+(,[^,]+)*$", message = "경로는 쉼표로 구분되어 있어야 합니다.")
    @Schema(description = "경로 목록", example = "곰상,울산역,부산역")
    private final String path;

    @NotBlank(message = "목적지를 입력해주세요.")
    @Schema(description = "목적지", example = "종착지")
    private final String destination;

    @NotNull(message = "총 좌석 수를 입력해주세요.")
    @Schema(description = "총 좌석 수", example = "45")
    private final Integer totalSeats;

    public HomeBus toEntity() {
        return HomeBus.builder()
                .label(label)
                .path(path.replaceAll(" ", ""))
                .destination(destination)
                .totalSeats(totalSeats)
                .build();
    }
}
