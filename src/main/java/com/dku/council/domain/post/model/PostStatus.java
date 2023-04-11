package com.dku.council.domain.post.model;

public enum PostStatus {
    /**
     * 활성화 상태
     */
    ACTIVE,

    /**
     * 운영자에 의해 가려져서, 운영자만 볼 수 있는 상태
     */
    BLINDED,

    /**
     * 작성자에 의해 삭제된 상태
     */
    DELETED,

    /**
     * 운영자에 의해 삭제된 상태
     */
    DELETED_BY_ADMIN
}
