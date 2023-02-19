package com.dku.council.domain.post.model.dto.response;

import lombok.Getter;

@Getter
public class ResponsePostIdDto {
    private final Long id;

    public ResponsePostIdDto(Long id) {
        this.id = id;
    }
}
