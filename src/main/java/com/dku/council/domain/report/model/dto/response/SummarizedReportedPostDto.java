package com.dku.council.domain.report.model.dto.response;

import com.dku.council.domain.post.model.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class SummarizedReportedPostDto {
    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "게시글 제목")
    private final String title;

    @Schema(description = "작성자", example = "익명")
    private final String author;

    @Schema(description = "최초 신고 시각")
    private final LocalDateTime firstReportAt;

    @Schema(description = "신고된 횟수", example = "2")
    private final int reportedCount;

    @Schema(description = "블라인드 여부", example = "true")
    private final boolean isBlinded;

    public SummarizedReportedPostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.author = post.getDisplayingUsername();
        this.firstReportAt = post.getCreatedAt();
        this.reportedCount = post.getReportedCount();
        this.isBlinded = post.isBlinded();
    }

}
