package com.dku.council.domain.like;

import com.dku.council.domain.like.repository.PostLikeMemoryRepository;
import com.dku.council.domain.like.repository.PostLikePersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeMemoryRepository memoryRepository;
    private final PostLikePersistenceRepository persistenceRepository;


    /**
     * '좋아요' 처리
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    public void like(Long postId, Long userId) {
        cacheLikesMembers(postId);
        memoryRepository.addLikeMemberAt(postId, userId);
        increaseLikeCount(postId);
    }

    private void increaseLikeCount(Long postId) {
        int count = memoryRepository.getCachedLikeCount(postId);
        if (count == -1) {
            count = persistenceRepository.countByPostId(postId);
            memoryRepository.setLikeCount(postId, count + 1);
        } else {
            memoryRepository.increaseLikeCount(postId);
        }
    }

    /**
     * '좋아요' 취소 처리
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    public void unlike(Long postId, Long userId) {
        cacheLikesMembers(postId);
        memoryRepository.removeLikeMemberAt(postId, userId);
        decreaseLikeCount(postId);
    }

    private void decreaseLikeCount(Long postId) {
        int count = memoryRepository.getCachedLikeCount(postId);
        if (count == -1) {
            count = persistenceRepository.countByPostId(postId);
            memoryRepository.setLikeCount(postId, count - 1);
        } else {
            memoryRepository.increaseLikeCount(postId);
        }
    }

    /**
     * '좋아요'를 했었는지 확인
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요를 했는지?
     */
    public boolean isLiked(Long postId, Long userId) {
        cacheLikesMembers(postId);
        return memoryRepository.containsLikeMemberAt(postId, userId);
    }

    private void cacheLikesMembers(Long postId) {
        if (!memoryRepository.isCachedPost(postId)) {
            List<Long> members = persistenceRepository.findAllByPostId(postId).stream()
                    .map(ent -> ent.getUser().getId())
                    .collect(Collectors.toList());
            memoryRepository.setLikeMembers(postId, members);
        }
    }

    /**
     * 게시글의 '좋아요' 개수 가져오기.
     * 메모리에 '좋아요' 개수가 없다면 캐싱한다.
     *
     * @param postId 게시글 ID
     * @return 좋아요 개수
     */
    public int getCountOfLikes(Long postId) {
        int count = memoryRepository.getCachedLikeCount(postId);
        if (count == -1) {
            count = persistenceRepository.countByPostId(postId);
            memoryRepository.setLikeCount(postId, count);
        }
        return count;
    }

    /**
     * 영속성 DB에 실제로 데이터를 반영한다.
     */
    public void dumpToDB() {
        System.out.println("DUMP");
    }
}
