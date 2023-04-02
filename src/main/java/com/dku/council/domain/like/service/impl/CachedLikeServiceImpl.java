package com.dku.council.domain.like.service.impl;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.model.entity.LikeElement;
import com.dku.council.domain.like.repository.LikeMemoryRepository;
import com.dku.council.domain.like.repository.LikePersistenceRepository;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CachedLikeServiceImpl implements LikeService {

    private final LikeMemoryRepository memoryRepository;
    private final UserRepository userRepository;
    private final LikePersistenceRepository persistenceRepository;


    @Override
    public void like(Long elementId, Long userId, LikeTarget target) {
        if (!isLiked(elementId, userId, target)) {
            memoryRepository.like(elementId, userId, target);
            memoryRepository.setLikeCount(elementId, getCountOfLikes(elementId, target) + 1, target); // TODO 동시성 문제
        }
    }

    @Override
    public void cancelLike(Long elementId, Long userId, LikeTarget target) {
        if (isLiked(elementId, userId, target)) {
            memoryRepository.cancelLike(elementId, userId, target);
            memoryRepository.setLikeCount(elementId, getCountOfLikes(elementId, target) - 1, target);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long elementId, Long userId, LikeTarget target) {
        Boolean liked = memoryRepository.isLiked(elementId, userId, target);
        if (liked == null) {
            liked = persistenceRepository.findByElementIdAndUserId(elementId, userId, target).isPresent();
            memoryRepository.setIsLiked(elementId, userId, target, liked);
        }
        return liked;
    }

    @Override
    public Page<Long> getLikedElementIds(Long userId, Pageable pageable, LikeTarget target) {
        dumpByUserId(userId, target);
        Page<LikeElement> likes = persistenceRepository.findAllByUserId(userId, target, pageable);
        return likes.map(LikeElement::getElementId);
    }

    private void dumpByUserId(Long userId, LikeTarget target) {
        List<LikeEntry> allLikes = memoryRepository.getAllLikesAndClear(userId, target);
        User user = userRepository.getReferenceById(userId);
        for (LikeEntry ent : allLikes) {
            if (ent.getState() == LikeState.LIKED) {
                persistenceRepository.save(new LikeElement(user, ent.getElementId(), target));
            } else {
                persistenceRepository.deleteByElementIdAndUserId(ent.getElementId(), userId, target);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getCountOfLikes(Long elementId, LikeTarget target) {
        int count = memoryRepository.getCachedLikeCount(elementId, target);
        if (count == -1) {
            count = persistenceRepository.countByElementIdAndTarget(elementId, target);
            memoryRepository.setLikeCount(elementId, count, target);
        }
        return count;
    }

    /**
     * 영속성 DB에 실제로 데이터를 반영한다.
     *
     * @return 처리한 entity 개수
     */
    @Transactional
    public long dumpToDB(LikeTarget target) {
        Map<Long, List<LikeEntry>> allLikes = memoryRepository.getAllLikesAndClear(target);
        for (Map.Entry<Long, List<LikeEntry>> ent : allLikes.entrySet()) {
            User user = userRepository.getReferenceById(ent.getKey());
            for (LikeEntry likeEntry : ent.getValue()) {
                if (likeEntry.getState() == LikeState.LIKED) {
                    persistenceRepository.save(new LikeElement(user, likeEntry.getElementId(), target));
                } else if (likeEntry.getState() == LikeState.CANCELLED) {
                    persistenceRepository.deleteByElementIdAndUserId(likeEntry.getElementId(), ent.getKey(), target);
                }
            }
        }

        return allLikes.size();
    }
}
