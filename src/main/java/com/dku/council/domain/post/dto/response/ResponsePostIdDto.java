package com.dku.council.domain.post.dto.response;

import lombok.Getter;

@Getter
public class ResponsePostIdDto {
    private final Long id;

    public ResponsePostIdDto(Long id) {
        this.id = id;
    }
}
