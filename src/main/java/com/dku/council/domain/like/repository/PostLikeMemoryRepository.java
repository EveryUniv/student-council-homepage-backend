package com.dku.council.domain.like.repository;

import com.dku.council.domain.like.model.LikeEntry;

import java.util.List;

public interface PostLikeMemoryRepository {

    /**
     * 게시글 '좋아요' 추가
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    void addPostLike(Long postId, Long userId);

    /**
     * 게시글 '좋아요' 취소 처리. 실제로 삭제되진않고 취소되었다고
     * 마킹만 해둔다. 그래야 나중에 캐싱되어있는지 확인할 수 있기 때문이다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    void removePostLike(Long postId, Long userId);

    /**
     * 메모리에서 사용자가 게시글에 '좋아요'를 눌렀는지 확인한다.
     * 캐싱되어있는 경우에는 true/false로 반환하지만, 캐싱되어있지 않다면 null을
     * 반환한다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 사용자가 좋아요를 눌렀는지 반환. 캐싱된 데이터가 없다면 null반환.
     */
    Boolean isPostLiked(Long postId, Long userId);

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

    /**
     * 메모리에 저장된 모든 '좋아요' 데이터를 가져온다.
     *
     * @return '좋아요' entities
     */
    List<LikeEntry> getAllPostLikes();
}
