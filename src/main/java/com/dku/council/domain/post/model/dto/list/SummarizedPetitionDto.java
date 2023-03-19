package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
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

    @Schema(description = "댓글 개수 (동의 인원)", example = "48")
    private final int commentCount;

    public SummarizedPetitionDto(SummarizedGenericPostDto dto, Petition petition, Duration expiresTime, int commentCount) {
        super(dto);
        this.status = petition.getExtraStatus();
        this.expiresAt = petition.getCreatedAt().plus(expiresTime).toLocalDate();
        this.commentCount = commentCount;
    }
}
