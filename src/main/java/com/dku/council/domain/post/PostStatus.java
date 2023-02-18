package com.dku.council.domain.post;

public enum PostStatus {
    /**
     * 활성화 상태
     */
    ACTIVE,

    /**
     * 작성자에 의해 삭제된 상태
     */
    DELETED,

    /**
     * 운영자에 의해 삭제된 상태
     */
    DELETED_BY_ADMIN
}
