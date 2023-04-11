package com.dku.council.domain.rental.model.dto.request;

import com.dku.council.domain.rental.model.RentalUserClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RequestCreateRentalDto {

    @NotNull
    @Schema(description = "물품 ID", example = "11")
    private final Long itemId;

    @NotNull
    @Schema(description = "대여자 구분")
    private final RentalUserClass userClass;

    @NotNull
    @Schema(description = "사용 시작 시각")
    private final LocalDateTime rentalStart;

    @NotNull
    @Schema(description = "사용 종료 시각")
    private final LocalDateTime rentalEnd;

    @NotNull
    @Schema(description = "행사명")
    private final String title;

    @NotNull
    @Schema(description = "행사 내용")
    private final String body;
}
