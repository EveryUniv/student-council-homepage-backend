package com.dku.council.domain.homebus.model;

public enum HomeBusStatus {
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
    NEED_CANCEL_APPROVAL
}
