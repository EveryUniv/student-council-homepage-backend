package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.tag.model.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
public class ResponseSingleGenericPostDto {

    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "게시글 제목", example = "제목")
    private final String title;

    @Schema(description = "게시글 본문", example = "본문")
    private final String body;

    @Schema(description = "작성자", example = "작성자")
    private final String author;

    @Schema(description = "카테고리", example = "학교생활")
    private final String category;

    @Schema(description = "생성 시각", example = "2022-03-01T11:31:11.444")
    private final LocalDateTime createdAt;

    @Schema(description = "파일 목록")
    private final List<PostFileDto> files;

    @Schema(description = "내가 쓴 게시물인지?", example = "true")
    private final boolean isMine;

    public ResponseSingleGenericPostDto(String baseFileUrl, Long userId, Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.author = post.getUser().getName();
        this.category = Optional.ofNullable(post.getTag())
                .map(Tag::getName)
                .orElse(null);
        this.createdAt = post.getCreatedAt();
        this.files = PostFileDto.listOf(baseFileUrl, post.getFiles());
        this.isMine = post.getUser().getId().equals(userId);
    }
}
