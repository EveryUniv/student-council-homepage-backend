package com.dku.council.domain.post.model.dto.page;

import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.List;

@Getter
public class SummarizedRuleDto {
    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "게시글 제목")
    private final String title;

    @Schema(description = "생성 날짜", example = "2022-01-01")
    private final String createdDate;

    @Schema(description = "파일 목록")
    private final List<PostFileDto> files;

    @Schema(description = "부서명", example = "총학생회")
    private final String department;

    @Schema(description = "조회수", example = "16")
    private final int views;

    public SummarizedRuleDto(MessageSource messageSource, String baseFileUrl, Rule rule) {
        this.id = rule.getId();
        this.title = rule.getTitle();
        this.createdDate = rule.getCreatedDateText();
        this.files = PostFileDto.listOf(baseFileUrl, rule.getFiles());
        this.department = rule.getUser().getMajor().getDepartmentName(messageSource);
        this.views = rule.getViews();
    }
}
