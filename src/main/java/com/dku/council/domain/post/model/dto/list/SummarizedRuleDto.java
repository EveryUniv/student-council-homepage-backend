package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.entity.posttype.Rule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Getter
public class SummarizedRuleDto extends SummarizedGenericPostDto {

    @Schema(description = "부서명 (소속대학)", example = "공과대학")
    private final String department;

    public SummarizedRuleDto(MessageSource messageSource, String baseFileUrl, int bodySize, Rule rule) {
        super(baseFileUrl, bodySize, rule);
        this.department = rule.getUser().getMajor().getDepartmentName(messageSource);
    }
}
