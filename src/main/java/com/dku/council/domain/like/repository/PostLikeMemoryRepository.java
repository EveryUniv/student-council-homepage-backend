package com.dku.council.domain.like.repository;

import java.util.List;

public interface PostLikeMemoryRepository {

    /**
     * 게시글 '좋아요' Set에 사용자 추가.
     * 게시글에 '좋아요'한 사용자 목록이 캐싱되어있어야 정상 동작한다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    void addLikeMemberAt(Long postId, Long userId);

    /**
     * 게시글 '좋아요' Set에서 사용자 제거.
     * 게시글에 '좋아요'한 사용자 목록이 캐싱되어있어야 정상 동작한다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    void removeLikeMemberAt(Long postId, Long userId);

    /**
     * 게시글 '좋아요' Set에 사용자가 포함되어있는지 확인
     * 게시글에 '좋아요'한 사용자 목록이 캐싱되어있어야 정상 동작한다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 사용자가 포함되어있는지?
     */
    boolean containsLikeMemberAt(Long postId, Long userId);

    /**
     * 게시글에 '좋아요'한 사용자 목록이 메모리에 캐싱되어있는지?
     *
     * @param postId 게시글 ID
     * @return 캐싱되어있는지?
     */
    boolean isCachedPost(Long postId);

    /**
     * 게시글에 '좋아요'한 사용자 목록을 캐싱
     *
     * @param postId  게시글 ID
     * @param members '좋아요'한 사용자 목록
     */
    void setLikeMembers(Long postId, List<Long> members);

    /**
     * 메모리에 캐싱된 게시글의 좋아요 개수 확인.
     *
     * @param postId 게시글 ID
     * @return 캐싱된 게시글의 좋아요 개수. 없으면 -1리턴.
     */
    int getCachedLikeCount(Long postId);

    /**
     * 게시글의 좋아요 개수 + 1.
     * 게시글의 좋아요 개수가 캐싱되어있어야 정상 동작한다.
     *
     * @param postId 게시글 ID
     */
    void increaseLikeCount(Long postId);

    /**
     * 게시글의 좋아요 개수 - 1.
     * 게시글의 좋아요 개수가 캐싱되어있어야 정상 동작한다.
     *
     * @param postId 게시글 ID
     */
    void decreaseLikeCount(Long postId);

    /**
     * 게시글의 좋아요 개수 캐싱
     *
     * @param postId 게시글 ID
     * @param count  좋아요 개수
     */
    void setLikeCount(Long postId, int count);
}
