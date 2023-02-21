package com.dku.council.domain.like;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.domain.like.repository.PostLikeMemoryRepository;
import com.dku.council.domain.like.repository.PostLikePersistenceRepository;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.PostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeMemoryRepository memoryRepository;
    private final PostLikePersistenceRepository persistenceRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    /**
     * '좋아요' 처리
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    public void like(Long postId, Long userId) {
        memoryRepository.addPostLike(postId, userId);
        increaseLikeCount(postId);
    }

    private void increaseLikeCount(Long postId) {
        doCachedLikeCountTask(postId,
                (count) -> memoryRepository.setLikeCount(postId, count + 1),
                () -> memoryRepository.increaseLikeCount(postId));
    }

    /**
     * '좋아요' 취소 처리
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    public void cancelLike(Long postId, Long userId) {
        memoryRepository.removePostLike(postId, userId);
        decreaseLikeCount(postId);
    }

    private void decreaseLikeCount(Long postId) {
        doCachedLikeCountTask(postId,
                (count) -> memoryRepository.setLikeCount(postId, count - 1),
                () -> memoryRepository.decreaseLikeCount(postId));
    }

    /**
     * '좋아요'를 했었는지 확인
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요를 했었는지 반환
     */
    public boolean isPostLiked(Long postId, Long userId) {
        Boolean liked = memoryRepository.isPostLiked(postId, userId);
        if (liked == null) {
            liked = persistenceRepository.findByPostIdAndUserId(postId, userId).isPresent();
            memoryRepository.removePostLike(postId, userId);
        }
        return liked;
    }

    /**
     * 게시글의 '좋아요' 개수 가져오기.
     * 메모리에 '좋아요' 개수가 없다면 캐싱한다.
     *
     * @param postId 게시글 ID
     * @return 좋아요 개수
     */
    public int getCountOfLikes(Long postId) {
        return doCachedLikeCountTask(postId,
                (count) -> memoryRepository.setLikeCount(postId, count),
                null);
    }

    private int doCachedLikeCountTask(Long postId, Consumer<Integer> onNotCached, Runnable onCached) {
        int count = memoryRepository.getCachedLikeCount(postId);
        if (count == -1) {
            count = persistenceRepository.countByPostId(postId);
            if (onNotCached != null) {
                onNotCached.accept(count);
            }
        } else if (onCached != null) {
            onCached.run();
        }
        return count;
    }

    /**
     * 영속성 DB에 실제로 데이터를 반영한다.
     */
    public void dumpToDB() {
        List<LikeEntry> allLikes = memoryRepository.getAllPostLikes();
        for (LikeEntry ent : allLikes) {
            if (ent.getState() == LikeState.LIKED) {
                User user = userRepository.getReferenceById(ent.getUserId());
                Post post = postRepository.getReferenceById(ent.getPostId());
                persistenceRepository.save(new PostLike(user, post));
            } else if (ent.getState() == LikeState.CANCELLED) {
                persistenceRepository.deleteByPostIdAndUserId(ent.getPostId(), ent.getUserId());
            }
        }
    }
}
