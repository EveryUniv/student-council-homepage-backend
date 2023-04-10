package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Getter
public class ResponsePetitionDto extends ResponseSingleGenericPostDto {

    @Schema(description = "청원 상태", example = "WAITING")
    private final PetitionStatus status;

    @Schema(description = "운영진 답변", example = "안녕하세요", nullable = true)
    private final String answer;

    @Schema(description = "청원 마감일")
    private final LocalDate expiresAt;

    @Schema(description = "동의 인원", example = "48")
    private final int agreeCount;

    @Schema(description = "동의 인원 분포")
    private final List<PetitionStatisticDto> statisticList;

    @Schema(hidden = true)
    @JsonIgnore
    private final int likes = 0;
    private final boolean isAgree;

    public ResponsePetitionDto(ResponseSingleGenericPostDto dto, Petition post, Duration expiresTime, int agreeCount, List<PetitionStatisticDto> statisticList, boolean isAgree) {
        super(dto);
        this.status = post.getExtraStatus();
        this.answer = post.getAnswer();
        this.expiresAt = post.getCreatedAt().plus(expiresTime).toLocalDate();
        this.agreeCount = agreeCount;
        this.statisticList = statisticList;
        this.isAgree = isAgree;
    }
}
