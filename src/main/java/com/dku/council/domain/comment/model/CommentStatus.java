package com.dku.council.domain.comment.model;

public enum CommentStatus {
    /**
     * 활성화 상태
     */
    ACTIVE,

    /**
     * 작성자에 의해 수정된 상태
     */
    EDITED,

    /**
     * 작성자에 의해 삭제된 상태
     */
    DELETED,

    /**
     * 운영자에 의해 삭제된 상태
     */
    DELETED_BY_ADMIN;

    public static final String ACTIVE_NAME = "ACTIVE";
}
