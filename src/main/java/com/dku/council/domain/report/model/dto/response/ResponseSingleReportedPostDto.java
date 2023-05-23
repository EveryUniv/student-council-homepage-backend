package com.dku.council.domain.report.model.dto.response;

import com.dku.council.domain.post.model.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ResponseSingleReportedPostDto {

    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "게시글 제목", example = "제목")
    private final String title;

    @Schema(description = "작성자", example = "작성자")
    private final String author;

    @Schema(description = "최초 신고 시각")
    private final LocalDateTime firstReportAt;

    @Schema(description = "신고된 횟수", example = "2")
    private final Long reportedCount;

    @Schema(description = "블라인드 여부", example = "true")
    private final boolean isBlinded;

    @Schema(description = "신고된 카테고리")
    private final List<ResponseReportCategoryCountDto> reportedCategory;

    public ResponseSingleReportedPostDto(Post post, Long reportedCount, List<ResponseReportCategoryCountDto> reportedCategory) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.author = post.getDisplayingUsername();
        this.firstReportAt = post.getCreatedAt();
        this.reportedCount = reportedCount;
        this.isBlinded = post.isBlinded();
        this.reportedCategory = reportedCategory;
    }
}
