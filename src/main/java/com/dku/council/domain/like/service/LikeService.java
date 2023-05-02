package com.dku.council.domain.like.service;

import com.dku.council.domain.like.model.LikeTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// TODO POST, Comment로 분리
public interface LikeService {

    /**
     * '좋아요' 처리
     *
     * @param elementId 요소 ID
     * @param userId    사용자 ID
     * @param target    요소 타입
     */
    void like(Long elementId, Long userId, LikeTarget target);

    /**
     * '좋아요' 취소 처리
     *
     * @param elementId 요소 ID
     * @param userId    사용자 ID
     * @param target    요소 타입
     */
    void cancelLike(Long elementId, Long userId, LikeTarget target);

    /**
     * '좋아요'를 했었는지 확인
     *
     * @param elementId 요소 ID
     * @param userId    사용자 ID
     * @param target    요소 타입
     * @return 좋아요를 했었는지 반환
     */
    boolean isLiked(Long elementId, Long userId, LikeTarget target);

    /**
     * '좋아요'누른 요소들 목록 가져오기
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @param target   요소 타입
     * @return '좋아요'누른 요소들
     */
    Page<Long> getLikedElementIds(Long userId, Pageable pageable, LikeTarget target);

    /**
     * '좋아요'누른 요소들 개수 가져오기
     *
     * @param userId 사용자 ID
     * @param target 요소 타입
     * @return '좋아요'누른 요소 개수
     */
    Long getCountOfLikedElements(Long userId, LikeTarget target);

    /**
     * '좋아요' 개수 가져오기.
     * 메모리에 '좋아요' 개수가 없다면 캐싱한다.
     *
     * @param elementId 요소 ID
     * @return 좋아요 개수
     */
    int getCountOfLikes(Long elementId, LikeTarget target);
}
