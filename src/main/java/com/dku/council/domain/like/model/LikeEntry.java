package com.dku.council.domain.like.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class LikeEntry {
    private final Long postId;
    private final Long userId;
    private final LikeState state;
}
