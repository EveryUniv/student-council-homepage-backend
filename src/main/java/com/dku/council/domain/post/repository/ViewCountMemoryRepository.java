package com.dku.council.domain.post.repository;

import java.time.Duration;
import java.time.Instant;

public interface ViewCountMemoryRepository {

    /**
     * 조회수 셋에 이미 존재하는지 확인한다.
     *
     * @param postId         게시글 ID
     * @param userIdentifier 유저 식별자(remoteAddress, Id 등..)
     * @param now            현재 시각
     * @return 이미 존재하면 true, 아니면 false반환.
     */
    boolean isAlreadyContains(Long postId, String userIdentifier, Instant now);

    /**
     * 조회수 셋에 새로 추가한다. 추가하고나서 expiresAfter분 뒤에 삭제된다.
     *
     * @param postId         게시글 ID
     * @param userIdentifier 유저 식별자(remoteAddress, Id 등..)
     * @param expiresAfter   캐시 유지시간.
     * @param now            현재 시각
     */
    void put(Long postId, String userIdentifier, Duration expiresAfter, Instant now);
}
