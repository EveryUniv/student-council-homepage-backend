package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SummarizedPetitionDto extends SummarizedGenericPostDto {

    @Schema(description = "청원 상태", example = "WAITING")
    private final PetitionStatus status;

    @Schema(description = "댓글 개수", example = "48")
    private final int commentCount;

    public SummarizedPetitionDto(SummarizedGenericPostDto dto, Petition petition, int commentCount) {
        super(dto);
        this.status = petition.getExtraStatus();
        this.commentCount = commentCount;
    }
}
