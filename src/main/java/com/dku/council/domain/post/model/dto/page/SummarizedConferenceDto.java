package com.dku.council.domain.post.model.dto.page;

import com.dku.council.domain.post.model.entity.posttype.Conference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SummarizedConferenceDto extends SummarizedGenericPostDto {

    @Schema(description = "회차", example = "4")
    private final int round;

    @Schema(description = "개최 일자", example = "2022-01-08")
    private final String date;

    public SummarizedConferenceDto(String baseFileUrl, int bodySize, Conference conference) {
        super(baseFileUrl, bodySize, conference);
        this.round = conference.getRound();
        this.date = conference.getDate().toString();
    }
}
