package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseGeneralForumDto extends ResponseSingleGenericPostDto {

    @Schema(description = "작성자 학과", example = "컴퓨터공학과")
    private final String authorMajor;

    public ResponseGeneralForumDto(ResponseSingleGenericPostDto dto, GeneralForum post) {
        super(dto);
        this.authorMajor = post.getUser().getMajor().getDepartment();
    }
}
