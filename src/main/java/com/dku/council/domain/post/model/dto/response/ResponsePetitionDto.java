package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Getter
public class ResponsePetitionDto extends ResponseSingleGenericPostDto {

    @Schema(description = "청원 상태", example = "WAITING")
    private final PetitionStatus status;

    @Schema(description = "운영진 답변", example = "안녕하세요", nullable = true)
    private final String answer;

    public ResponsePetitionDto(MessageSource messageSource, String baseFileUrl, Long userId, Petition post) {
        super(messageSource, baseFileUrl, userId, post);
        this.status = post.getPetitionStatus();
        this.answer = post.getAnswer();
    }
}
