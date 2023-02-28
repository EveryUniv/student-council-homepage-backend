package com.dku.council.global.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseIdDto {
    private Long id;

    public ResponseIdDto(Long id) {
        this.id = id;
    }
}
