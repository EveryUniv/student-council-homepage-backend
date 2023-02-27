package com.dku.council.domain.post.model.dto.page;

import com.dku.council.domain.post.model.entity.posttype.Rule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Getter
public class SummarizedRuleDto extends SummarizedGenericPostDto {

    @Schema(description = "부서명", example = "총학생회")
    private final String department;

    public SummarizedRuleDto(MessageSource messageSource, String baseFileUrl, Rule rule) {
        super(baseFileUrl, rule);
        this.department = rule.getUser().getMajor().getDepartmentName(messageSource);
    }
}
