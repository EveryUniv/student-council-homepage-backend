package com.dku.council.domain.post.model.dto.list;

import com.dku.council.domain.post.model.VocStatus;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SummarizedVocDto extends SummarizedGenericPostDto {

    @Schema(description = "VOC 상태", example = "WAITING")
    private final VocStatus status;

    public SummarizedVocDto(String baseFileUrl, Voc voc, int bodySize) {
        super(baseFileUrl, bodySize, voc);
        this.status = voc.getExtraStatus();
    }
}
