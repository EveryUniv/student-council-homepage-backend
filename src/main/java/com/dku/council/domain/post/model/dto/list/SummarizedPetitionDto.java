package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
public class SummarizedPetitionDto extends SummarizedGenericPostDto {

    @Schema(description = "청원 상태", example = "WAITING")
    private final PetitionStatus status;

    @Schema(description = "청원 마감일")
    private final LocalDate expiresAt;

    @Schema(description = "동의 인원 수", example = "48")
    private final int agreeCount;

    @Schema(hidden = true)
    @JsonIgnore
    private final int commentCount = 0;

    @Schema(hidden = true)
    @JsonIgnore
    private final int likes = 0;

    public SummarizedPetitionDto(SummarizedGenericPostDto dto, Petition petition, Duration expiresTime, int agreeCount) {
        super(dto);
        this.status = petition.getExtraStatus();
        this.expiresAt = petition.getCreatedAt().plus(expiresTime).toLocalDate();
        this.agreeCount = agreeCount;
    }
}
