package com.dku.council.domain.post.model.dto.page;

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

    public SummarizedPetitionDto(String baseFileUrl, Petition petition, int bodySize, int commentCount) {
        super(baseFileUrl, bodySize, petition);
        this.status = petition.getPetitionStatus();
        this.commentCount = commentCount;
    }
}
