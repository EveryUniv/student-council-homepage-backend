package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.report.model.entity.Report;
import com.dku.council.domain.report.model.entity.ReportCategory;
import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestCreateReportDto {

    @Schema(description = "신고 카테고리", example = "PROFANITY")
    private final String categoryName;

    public Report toEntity(User user, Post post) {
        return new Report(user, post, ReportCategory.fromValue(categoryName));
    }
}
