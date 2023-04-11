package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.tag.model.dto.TagDto;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Schema(description = "태그 목록")
    private final List<TagDto> tag;

    @Schema(description = "생성 시각")
    private final LocalDateTime createdAt;

    @Schema(description = "파일 목록")
    private final List<PostFileDto> files;

    @Schema(description = "좋아요 수", example = "16")
    private final int likes;

    @Schema(description = "조회수", example = "55")
    private final int views;

    @Schema(description = "내가 쓴 게시물인지?", example = "true")
    private final boolean isMine;

    @Schema(description = "내가 좋아요를 눌렀는지?", example = "false")
    private final boolean isLiked;

    @Schema(description = "블라인드 여부", example = "false")
    private final boolean isBlinded;

    public ResponseSingleGenericPostDto(ObjectUploadContext context, int likes, boolean isMine, boolean isLiked, Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.author = post.getDisplayingUsername();
        this.tag = post.getPostTags().stream()
                .map(e -> new TagDto(e.getTag()))
                .collect(Collectors.toList());
        this.likes = likes;
        this.views = post.getViews();
        this.createdAt = post.getCreatedAt();
        this.files = PostFileDto.listOf(context, post.getFiles());
        this.isMine = isMine;
        this.isLiked = isLiked;
        this.isBlinded = post.isBlinded();
    }

    public ResponseSingleGenericPostDto(ResponseSingleGenericPostDto copy) {
        this.id = copy.id;
        this.title = copy.title;
        this.body = copy.body;
        this.author = copy.author;
        this.tag = copy.tag;
        this.likes = copy.likes;
        this.views = copy.views;
        this.createdAt = copy.createdAt;
        this.files = copy.files;
        this.isMine = copy.isMine;
        this.isLiked = copy.isLiked;
        this.isBlinded = copy.isBlinded;
    }

}
