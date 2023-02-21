package com.dku.council.domain.like.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LikeEntry {
    private final Long postId;
    private final Long userId;
    private final LikeState state;
}
