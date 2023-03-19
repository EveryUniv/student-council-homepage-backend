package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.entity.posttype.Rule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SummarizedRuleDto extends SummarizedGenericPostDto {

    @Schema(description = "부서명 (소속대학)", example = "공과대학")
    private final String department;

    public SummarizedRuleDto(SummarizedGenericPostDto dto, Rule rule) {
        super(dto);
        this.department = rule.getUser().getMajor().getDepartment();
    }
}
