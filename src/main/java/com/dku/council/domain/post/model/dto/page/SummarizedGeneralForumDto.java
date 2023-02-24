package com.dku.council.domain.post.model.dto.page;

import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class SummarizedGeneralForumDto {
    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "카테고리", example = "기타")
    private final String category;

    @Schema(description = "제목", example = "게시글 제목")
    private final String title;

    @Schema(description = "생성 날짜", example = "2022-01-01")
    private final String createdDate;

    @Schema(description = "파일 목록")
    private final List<PostFileDto> files;

    @Schema(description = "조회수", example = "16")
    private final int views;

    @Schema(description = "댓글수", example = "5")
    private final int comments;

    public SummarizedGeneralForumDto(String baseFileUrl, GeneralForum generalForum) {
        this.id = generalForum.getId();
        this.category = generalForum.getCategory().getName();
        this.title = generalForum.getTitle();
        this.createdDate = generalForum.getCreatedDateText();
        this.files = PostFileDto.listOf(baseFileUrl, generalForum.getFiles());
        this.views = generalForum.getViews();
        this.comments = generalForum.getComments().size();
    }
}
