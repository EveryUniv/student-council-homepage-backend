package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SummarizedGeneralForumDto extends SummarizedGenericPostDto {

    @Schema(hidden = true)
    @JsonIgnore
    private final int likes = 0;

    public SummarizedGeneralForumDto(SummarizedGenericPostDto dto, GeneralForum post) {
        super(dto);
    }
}
