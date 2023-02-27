package com.dku.council.domain.post.model.dto.page;

import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class SummarizedGenericPostDto {

    // TODO 이것도 example 포함해서 javadoc으로 표현할 수 있도록 하기
    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "게시글 제목")
    private final String title;

    @Schema(description = "생성 날짜", example = "2022-01-01")
    private final String createdDate;

    @Schema(description = "파일 목록")
    private final List<PostFileDto> files;

    @Schema(description = "조회수", example = "16")
    private final int views;

    public SummarizedGenericPostDto(String baseFileUrl, Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.createdDate = post.getCreatedDateText();
        this.files = PostFileDto.listOf(baseFileUrl, post.getFiles());
        this.views = post.getViews();
    }
}
