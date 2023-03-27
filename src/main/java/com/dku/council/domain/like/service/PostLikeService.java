package com.dku.council.domain.like.service;

public interface PostLikeService {

    /**
     * '좋아요' 처리
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    void like(Long postId, Long userId);

    /**
     * '좋아요' 취소 처리
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    void cancelLike(Long postId, Long userId);

    /**
     * '좋아요'를 했었는지 확인
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요를 했었는지 반환
     */
    boolean isPostLiked(Long postId, Long userId);

    /**
     * 게시글의 '좋아요' 개수 가져오기.
     * 메모리에 '좋아요' 개수가 없다면 캐싱한다.
     *
     * @param postId 게시글 ID
     * @return 좋아요 개수
     */
    int getCountOfLikes(Long postId);
}
