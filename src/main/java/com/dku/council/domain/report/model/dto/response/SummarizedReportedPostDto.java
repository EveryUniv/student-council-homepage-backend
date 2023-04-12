package com.dku.council.domain.report.model.dto.response;

import com.dku.council.domain.report.model.entity.Report;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
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

    public SummarizedReportedPostDto(Report report) {
        this.id = report.getPost().getId();
        this.title = report.getPost().getTitle();
        this.author = report.getPost().getDisplayingUsername();
        this.firstReportAt = report.getPost().getCreatedAt();
        this.reportedCount = report.getPost().getReportedCount();
        this.isBlinded = report.getPost().isBlinded();
    }

}
