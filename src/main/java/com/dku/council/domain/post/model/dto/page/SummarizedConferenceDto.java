package com.dku.council.domain.post.model.dto.page;

import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.posttype.Conference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class SummarizedConferenceDto {

    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "회차", example = "4")
    private final int round;

    @Schema(description = "제목", example = "게시글 제목")
    private final String title;

    @Schema(description = "개최 일자", example = "2022-01-08")
    private final String date;

    @Schema(description = "생성 날짜", example = "2022-01-13")
    private final String createdDate;

    @Schema(description = "파일 목록")
    private final List<PostFileDto> files;

    public SummarizedConferenceDto(String baseFileUrl, Conference conference) {
        this.id = conference.getId();
        this.round = conference.getRound();
        this.title = conference.getTitle();
        this.date = conference.getDate().toString();
        this.createdDate = conference.getCreatedDateText();
        this.files = PostFileDto.listOf(baseFileUrl, conference.getFiles());
    }
}
