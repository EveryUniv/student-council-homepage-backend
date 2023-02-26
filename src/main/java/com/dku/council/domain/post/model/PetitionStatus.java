package com.dku.council.domain.post.model;

public enum PetitionStatus {
    /**
     * 답변 대기
     */
    WAITING,

    /**
     * 진행중
     */
    ACTIVE,

    /**
     * 답변 완료
     */
    ANSWERED,

    /**
     * 기간 만료
     */
    EXPIRED
}
