package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

// TODO 작성자 추가
@Getter
public class SummarizedGenericPostDto {

    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "게시글 제목")
    private final String title;

    @Schema(description = "본문", example = "게시글 본문")
    private final String body;

    @Schema(description = "생성 날짜", example = "2022-01-01")
    private final LocalDate createdAt;

    @Schema(description = "파일 목록")
    private final List<PostFileDto> files;

    @Schema(description = "좋아요 수", example = "26")
    private final int likes;

    @Schema(description = "조회수", example = "16")
    private final int views;

    public SummarizedGenericPostDto(String baseFileUrl, int bodySize, int likes, Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = slice(post.getBody(), bodySize);
        this.createdAt = post.getCreatedAt().toLocalDate();
        this.likes = likes;
        this.files = PostFileDto.listOf(baseFileUrl, post.getFiles());
        this.views = post.getViews();
    }

    public SummarizedGenericPostDto(SummarizedGenericPostDto copy) {
        this.id = copy.getId();
        this.title = copy.getTitle();
        this.body = copy.getBody();
        this.createdAt = copy.getCreatedAt();
        this.likes = copy.getLikes();
        this.files = copy.getFiles();
        this.views = copy.getViews();
    }

    private static String slice(String text, int maxLen) {
        return text.substring(0, Math.min(text.length(), maxLen));
    }
}
