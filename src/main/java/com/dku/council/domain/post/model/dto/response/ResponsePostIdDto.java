package com.dku.council.domain.post.model.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponsePostIdDto {
    private Long id;

    public ResponsePostIdDto(Long id) {
        this.id = id;
    }
}
