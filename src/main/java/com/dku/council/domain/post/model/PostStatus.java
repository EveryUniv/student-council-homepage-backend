package com.dku.council.domain.post.model;

public enum PostStatus {
    /**
     * 활성화 상태
     */
    ACTIVE,

    /**
     * 운영자에 의해 가려져서, 운영자만 볼 수 있는 상태
     * todo 게시글 list시 옵션 추가해서 운영자인 경우에는 blind 게시글도 볼 수 있게 하기
     * todo 게시글 상태 바꾸는 api 추가 (blind포함)
     */
    BLINDED,

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
