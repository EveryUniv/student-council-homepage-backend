package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.VocStatus;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseVocDto extends ResponseSingleGenericPostDto {

    @Schema(description = "VOC 상태", example = "WAITING")
    private final VocStatus status;

    @Schema(description = "운영진 답변", example = "안녕하세요", nullable = true)
    private final String answer;

    public ResponseVocDto(ResponseSingleGenericPostDto dto, Voc post) {
        super(dto);
        this.status = post.getExtraStatus();
        this.answer = post.getAnswer();
    }
}
