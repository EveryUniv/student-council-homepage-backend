package com.dku.council.domain.homebus.model;

public enum HomeBusStatus {
    /**
     * 신청 가능. (기본 상태)
     */
    NONE,

    /**
     * 승인 대기
     */
    NEED_APPROVAL,

    /**
     * 승인되어 발급된 상태
     */
    ISSUED,

    /**
     * 취소 승인 대기
     */
    NEED_CANCEL_APPROVAL,

    /**
     * 취소 승인 완료
     */
    CANCELLED
}
