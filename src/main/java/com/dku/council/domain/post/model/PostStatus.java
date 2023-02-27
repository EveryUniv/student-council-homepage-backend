package com.dku.council.domain.post.model;

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
    DELETED_BY_ADMIN;


    /**
     * 삭제된 상태인지?
     */
    public boolean isDeleted() {
        return this == DELETED || this == DELETED_BY_ADMIN;
    }
}
