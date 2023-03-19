package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
public class ResponsePetitionDto extends ResponseSingleGenericPostDto {

    @Schema(description = "청원 상태", example = "WAITING")
    private final PetitionStatus status;

    @Schema(description = "운영진 답변", example = "안녕하세요", nullable = true)
    private final String answer;

    @Schema(description = "청원 마감일")
    private final LocalDate expiresAt;

    public ResponsePetitionDto(ResponseSingleGenericPostDto dto, Petition post, Duration expiresTime) {
        super(dto);
        this.status = post.getExtraStatus();
        this.answer = post.getAnswer();
        this.expiresAt = post.getCreatedAt().plus(expiresTime).toLocalDate();
    }
}
