package com.dku.council.domain.post.model.dto.page;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SummarizedGeneralForumDto extends SummarizedGenericPostDto {

    @Schema(description = "카테고리", example = "기타")
    private final String category;

    // TODO DB Lazy loading 없이 바로 count 가져오기
    @Schema(description = "댓글수", example = "5")
    private final int comments;

    public SummarizedGeneralForumDto(String baseFileUrl, GeneralForum generalForum) {
        super(baseFileUrl, generalForum);
        this.category = generalForum.getCategory().getName();
        this.comments = generalForum.getComments().size();
    }
}
